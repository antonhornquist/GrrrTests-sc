GRControllerTests : Test {
	setup {
		GRTestsHelper.saveGlobals;
		GRTestsHelper.disableTraceAndFlash;
	}

	teardown {
		GRTestsHelper.restoreGlobals;
		GRController.all.do { |controller| controller.remove };
	}

	// initialization
	test_a_controller_should_by_default_create_a_new_top_view_of_same_bounds_as_controller_if_no_view_is_supplied_during_creation {
		var controller = MockController.new(8, 8);

		this.assert(controller.isAttached);
		this.assertEqual(
			controller.numCols,
			controller.view.numCols
		);
		this.assertEqual(
			controller.numRows,
			controller.view.numRows
		);
		this.assertEqual(
			Point.new(0, 0),
			controller.origin
		);
	}

	test_it_should_be_possible_to_override_default_creation_of_new_top_view_in_order_to_create_a_detached_controller {
		var controller = MockController.new(8, 8, nil, nil, false);

		this.assert(controller.isDetached);
	}

	test_it_should_be_possible_to_create_a_new_detached_controller_from_scratch {
		var controller = MockController.newDetached(8, 8);

		this.assert(controller.isDetached);
	}	

	test_it_should_be_possible_to_attach_a_controller_to_an_existing_view_during_creation_of_the_controller {
		var view = GRView.newDetached(4, 4);
		var controller = MockController.new(4, 4, view, Point.new(0, 0));

		this.assert(controller.isAttached);
		this.assertEqual(
			view,
			controller.view
		);
	}	

	test_all_created_controllers_should_be_available_in_a_controller_collection_class_variable {
		var controller1 = MockController.new(8, 8);
		var controller2 = MockController.new(3, 8);
		var controller3 = MockController.new(3, 3);
		var controller4 = MockController.new(1, 6);

		this.assertEqual(
			[controller1, controller2, controller3, controller4],
			GRController.all
		);
	}

	test_when_a_controller_is_created_init_action_should_be_invoked {
		var result = nil;
		var controller;

		GRController.initAction = { |controller|
			result = controller
		};

		controller = MockController.new(8, 8);

		this.assertEqual(
			controller, 
			result
		);
	}

	// attach / detach view
	test_it_should_be_possible_to_attach_a_detached_controller_to_a_view_of_same_bounds_as_controller {
		var view = GRView.newDetached(4, 4);
		var controller = MockController.newDetached(4, 4);

		controller.attach(view, Point.new(0, 0));

		this.assert(controller.isAttached);
		this.assertEqual(
			view,
			controller.view
		);
	}

	test_it_should_be_possible_to_attach_a_detached_controller_to_a_view_that_is_larger_than_the_controller_as_long_as_the_controller_is_within_view_bounds {
		var view = GRView.newDetached(8, 8);
		var controller = MockController.newDetached(4, 4);

		controller.attach(view, Point.new(2, 2));

		this.assert(controller.isAttached);
		this.assertEqual(
			view,
			controller.view
		);
	}

	test_if_a_controller_that_is_attached_to_a_view_at_a_specific_origin_is_out_of_bounds_of_the_view_an_error_should_occur {
		var view = GRView.newDetached(4, 4);
		var controller = MockController.newDetached(4, 4);

		this.assertErrorThrown(Error) { 
			controller.attach(view, Point.new(2, 2))
		};
	}

	test_attaching_a_view_to_an_already_attached_controller_should_throw_an_error {
		var view1 = GRView.newDetached(4, 4);
		var view2 = GRView.newDetached(4, 4);
		var controller = MockController.newDetached(4, 4);

		controller.attach(view1, Point.new(0, 0));

		this.assertErrorThrown(Error) { 
			controller.attach(view2, Point.new(0, 0))
		};
	}

	test_it_should_be_possible_to_detach_an_attached_controller_from_a_view {
		var view = GRView.newDetached(4, 4);
		var controller = MockController.new(4, 4, view, Point.new(0, 0));

		controller.detach;

		this.assert(controller.isDetached);
		this.assertEqual(
			nil,
			controller.view
		);
	}

	test_detaching_an_already_detached_controller_should_throw_an_error {
		var controller = MockController.newDetached(4, 4);

		this.assertErrorThrown(Error) { controller.detach };
	}

	// button and led state
	test_the_led_state_of_an_attached_controller_should_match_the_led_state_of_the_attached_view {
		var view = MockOddColsLitView.newDetached(4, 4);
		var controller = MockController.new(2, 2, view, Point.new(1, 1));

		this.assertEqual( true, controller.isLitAt(Point.new(0, 0)) );
		this.assertEqual( false, controller.isLitAt(Point.new(1, 0)) );
		this.assertEqual( true, controller.isLitAt(Point.new(0, 1)) );
		this.assertEqual( false, controller.isLitAt(Point.new(1, 1)) );
	}

	test_all_leds_of_a_detached_controller_should_be_unlit {
		var controller = MockController.newDetached(2, 2);

		this.assertEqual( false, controller.isLitAt(Point.new(0, 0)) );
		this.assertEqual( false, controller.isLitAt(Point.new(1, 0)) );
		this.assertEqual( false, controller.isLitAt(Point.new(0, 1)) );
		this.assertEqual( false, controller.isLitAt(Point.new(1, 1)) );
	}

	test_an_out_of_bounds_led_state_check_should_throw_an_error {
		var controller = MockController.new(4, 4);

		this.assertErrorThrown(Error) {
			controller.isLitAt(Point.new(4, 4))
		};
	}

	test_the_button_state_of_an_attached_controller_should_match_the_button_state_of_the_attached_view {
		var view = MockOddColsLitView.newDetached(4, 4);
		var controller = MockController.new(2, 2, view, Point.new(1, 1));

		view.press(Point.new(1, 2));
		view.press(Point.new(2, 2));

		this.assertEqual( false, controller.isPressedAt(Point.new(0, 0)) );
		this.assertEqual( false, controller.isPressedAt(Point.new(1, 0)) );
		this.assertEqual( true, controller.isPressedAt(Point.new(0, 1)) );
		this.assertEqual( true, controller.isPressedAt(Point.new(1, 1)) );
	}

	test_all_buttons_of_a_detached_controller_should_be_released {
		var controller = MockController.newDetached(2, 2);

		this.assertEqual( false, controller.isPressedAt(Point.new(0, 0)) );
		this.assertEqual( false, controller.isPressedAt(Point.new(1, 0)) );
		this.assertEqual( false, controller.isPressedAt(Point.new(0, 1)) );
		this.assertEqual( false, controller.isPressedAt(Point.new(1, 1)) );
	}

	test_an_out_of_bounds_button_state_check_should_throw_an_error {
		var controller = MockController.new(4, 4);

		this.assertErrorThrown(Error) {
			controller.isPressedAt(Point.new(4, 4))
		};
	}

	// emit button events
	test_when_a_controller_is_pressed_it_should_emit_button_events_to_its_attached_view {
		var view = GRView.newDetached(8, 8);
		var controller = MockController.new(4, 4, view, Point.new(1, 1));

		controller.emitPress(Point.new(2, 3));

		this.assert(view.isPressedAt(Point.new(3, 4)));
	}

	test_emitting_a_button_event_out_of_bounds_of_controller_should_throw_an_error {
		var view = GRView.newDetached(8, 8);
		var controller = MockController.new(4, 4, view, Point.new(2, 2));

		this.assertErrorThrown(Error) {
			controller.emitPress(Point.new(4, 4))
		};
	}

	// refreshing controller
	test_it_should_be_possible_to_refresh_a_controller_that_is_attached_to_a_view {
		var view = MockOddColsLitView.newDetached(4, 4);
		var controller = MockController.new(2, 2, view, Point.new(1, 1));

		controller.refresh;

		this.assertEqual(
			[	
				( point: Point.new(0, 0), on: true ),
				( point: Point.new(1, 0), on: false ),
				( point: Point.new(0, 1), on: true ),
				( point: Point.new(1, 1), on: false ),
			],
			controller.viewLedRefreshedNotifications
		);
	}

	test_it_should_be_possible_to_refresh_a_detached_controller {
		var controller = MockController.newDetached(2, 2);

		controller.refresh;

		this.assertEqual(
			[	
				( point: Point.new(0, 0), on: false ),
				( point: Point.new(1, 0), on: false ),
				( point: Point.new(0, 1), on: false ),
				( point: Point.new(1, 1), on: false ),
			],
			controller.viewLedRefreshedNotifications
		);
	}

	test_when_a_controller_is_attached_to_a_view_the_controller_should_be_refreshed_with_the_led_state_of_the_attached_view {
		var view = MockOddColsLitView.newDetached(4, 4);
		var controller = MockController.newDetached(2, 2);

		controller.attach(view, Point.new(1, 1));

		this.assertEqual(
			[	
				( point: Point.new(0, 0), on: true ),
				( point: Point.new(1, 0), on: false ),
				( point: Point.new(0, 1), on: true ),
				( point: Point.new(1, 1), on: false ),
			],
			controller.viewLedRefreshedNotifications
		);
	}

	test_when_a_controller_is_detached_from_a_view_the_controller_should_be_refreshed {
		var view = MockOddColsLitView.newDetached(4, 4);
		var controller = MockController.new(2, 2, view, Point.new(1, 1));

		controller.detach;

		this.assertEqual(
			[	
				( point: Point.new(0, 0), on: false ),
				( point: Point.new(1, 0), on: false ),
				( point: Point.new(0, 1), on: false ),
				( point: Point.new(1, 1), on: false ),
			],
			controller.viewLedRefreshedNotifications
		);
	}

	// removal
	test_when_a_controller_is_removed_it_should_no_be_available_in_the_controller_collection_class_variable {
		var controller1 = MockController.new(8, 8);
		var controller2 = MockController.new(3, 8);
		var controller3 = MockController.new(3, 3);
		var controller4 = MockController.new(1, 6);

		controller3.remove;

		this.assertEqual(
			[controller1, controller2, controller4],
			GRController.all
		);
	}

	test_when_a_controller_is_removed_it_should_get_detached_from_its_top_view {
		var controller = MockController.new(8, 8);

		controller.remove;

		this.assert(controller.isDetached);
	}

	test_when_a_controller_is_removed_it_should_invoke_on_remove_action {
		var result = nil;
		var controller = MockController.new(8, 8);
		controller.onRemove = {
			result = "i've been removed"
		};

		controller.remove;

		this.assertEqual(
			"i've been removed",
			result
		);
	}

	// view events
	test_when_a_view_attached_to_a_controller_is_refreshed_controller_should_receive_notifications_of_refreshed_leds_within_the_controllers_bounds {
		var view = MockOddColsLitView.newDetached(4, 4);
		var controller = MockController.new(2, 2, view, Point.new(1, 1));

		view.refresh;

		this.assertEqual(
			[	
				( point: Point.new(0, 0), on: true ),
				( point: Point.new(1, 0), on: false ),
				( point: Point.new(0, 1), on: true ),
				( point: Point.new(1, 1), on: false ),
			],
			controller.viewLedRefreshedNotifications
		);
	}

	test_when_a_view_attached_to_a_controller_receives_button_events_within_the_controllers_bounds_the_controller_should_receive_notifications_of_changes_in_button_state {
		var view = GRView.newDetached(4, 4);
		var controller = MockController.new(3, 3, view, Point.new(1, 1));

		view.press(Point.new(0, 0)); // not within controller bounds
		view.press(Point.new(0, 1)); // not within controller bounds
		view.press(Point.new(1, 0)); // not within controller bounds
		view.press(Point.new(2, 3));
		view.press(Point.new(3, 3));
		view.release(Point.new(2, 3));
		view.release(Point.new(3, 3));

		this.assertEqual(
			[	
				( point: Point.new(1, 2), pressed: true ),
				( point: Point.new(2, 2), pressed: true ),
				( point: Point.new(1, 2), pressed: false ),
				( point: Point.new(2, 2), pressed: false ),
			],
			controller.viewButtonStateChangedNotifications
		);
	}
}
