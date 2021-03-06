GRViewTests : Test {
	setup {
		GRTestsHelper.saveGlobals;
		GRTestsHelper.disableTraceAndFlash;
	}

	teardown {
		GRTestsHelper.restoreGlobals;
	}

	// initialization
	test_a_view_should_by_default_be_enabled {
		var view = GRView.new;
		this.assert(view.isEnabled);
	}

	test_a_view_created_with_new_disabled_should_be_disabled {
		var view = GRView.newDisabled;
		this.assert(view.isDisabled);
	}

	test_a_view_created_with_new_detached_should_be_detached {
		var view = GRView.newDetached(4, 4);
		this.assert(view.isDetached);
	}

	test_if_only_num_cols_is_specified_on_view_creation_num_rows_should_be_set_to_specified_num_cols_value {
		var view = GRView.new(nil, nil, 1);
		var detachedView = GRView.newDetached(2);
		var disabledView = GRView.newDisabled(nil, nil, 3);
		this.assertEqual(1, view.numCols);
		this.assertEqual(1, view.numRows);
		this.assertEqual(2, detachedView.numCols);
		this.assertEqual(2, detachedView.numRows);
		this.assertEqual(3, disabledView.numCols);
		this.assertEqual(3, disabledView.numRows);
	}

	test_it_should_not_be_possible_to_create_a_view_smaller_than_1x1 {
		this.assertErrorThrown(Error) { GRView.newDetached(0, 1) };
		this.assertErrorThrown(Error) { GRView.newDetached(1, 0) };
		this.assertErrorThrown(Error) { GRView.newDetached(0, 0) };
	}

	// id
	test_id {
		var view = GRView.new;
		view.id = \test;
		this.assertEqual(\test, view.id);
	}

	// enable / disable
	test_it_should_be_possible_to_disable_enabled_views {
		var view = GRView.new;
		view.disable;
		this.assert( view.isDisabled );
	}

	test_it_should_be_possible_to_enable_disabled_views {
		var view = GRView.newDisabled(nil, nil, 4, 4);
		view.enable;
		this.assert( view.isEnabled );
	}

	test_it_should_be_possible_to_get_notified_of_when_a_view_is_enabled_by_adding_an_action_to_a_view {
		var view = GRView.newDisabled(nil, nil, 4, 4);
		var listener = MockViewWasEnabledListener.new(view);
		view.enable;
		this.assert( listener.hasBeenNotifiedOf( [ [view] ] ) );
	}

	test_it_should_be_possible_to_get_notified_of_when_a_view_is_disabled_by_adding_an_action_to_a_view {
		var view = GRView.new;
		var listener = MockViewWasDisabledListener.new(view);
		view.disable;
		this.assert( listener.hasBeenNotifiedOf( [ [view] ] ) );
	}

	// bounds
	test_it_should_be_possible_to_retrieve_the_bounds_of_and_number_of_buttons_on_a_view {
		var view = GRView.newDetached(2, 3);
		this.assertEqual(2, view.numCols);
		this.assertEqual(3, view.numRows);
		this.assertEqual(6, view.numViewButtons);
	}

	test_it_should_be_possible_to_retrieve_points_of_a_view_starting_from_an_origin {
		this.assertEqual(
			[
				Point.new(10, 20), Point.new(11, 20),
				Point.new(10, 21), Point.new(11, 21),
				Point.new(10, 22), Point.new(11, 22)
			],
			GRView.newDetached(2, 3).asPointsFrom(Point.new(10, 20))
		);
	}

	test_it_should_be_possible_to_convert_bounds_to_points {
		this.assertEqual(
			[
				Point.new(5, 2), Point.new(6, 2), Point.new(7, 2), Point.new(8, 2), Point.new(9, 2),
				Point.new(5, 3), Point.new(6, 3), Point.new(7, 3), Point.new(8, 3), Point.new(9, 3),
				Point.new(5, 4), Point.new(6, 4), Point.new(7, 4), Point.new(8, 4), Point.new(9, 4),
			],
			GRView.boundsToPoints(Point.new(5, 2), 5, 3)
		);
	}

	test_it_should_be_possible_to_calculate_the_intersect_of_two_point_arrays {
		this.assertEqual(
			[
				Point.new(2, 1),
				Point.new(2, 2),
				Point.new(2, 3)
			],
			GRView.pointsSect(
				[
					Point.new(0, 0), Point.new(1, 0), Point.new(2, 0),
					Point.new(0, 1), Point.new(1, 1), Point.new(2, 1),
					Point.new(0, 2), Point.new(1, 2), Point.new(2, 2),
					Point.new(0, 3), Point.new(1, 3), Point.new(2, 3),
				],
				[
					Point.new(2, 1), Point.new(3, 1), Point.new(4, 1),
					Point.new(2, 2), Point.new(3, 2), Point.new(4, 2),
					Point.new(2, 3), Point.new(3, 3), Point.new(4, 3),
					Point.new(2, 4), Point.new(3, 4), Point.new(4, 4),
				];
			)
		);
	}

	test_it_should_be_possible_to_determine_if_a_specified_bounds_contains_a_specified_point {
		this.assert(
			GRView.boundsContainPoint(Point.new(2, 3), 3, 2, Point.new(3, 4))
		);
		this.assertEqual(
			false,
			GRView.boundsContainPoint(Point.new(2, 3), 3, 2, Point.new(1, 1))
		);
	}

	test_it_should_be_possible_to_retrieve_the_left_top_right_top_left_bottom_and_right_bottom_points_of_a_view {
		this.assertEqual(
			Point.new(0, 0),
			GRView.newDetached(2, 3).leftTopPoint
		);
		this.assertEqual(
			Point.new(1, 0),
			GRView.newDetached(2, 3).rightTopPoint
		);
		this.assertEqual(
			Point.new(0, 2),
			GRView.newDetached(2, 3).leftBottomPoint
		);
		this.assertEqual(
			Point.new(1, 2),
			GRView.newDetached(2, 3).rightBottomPoint
		);
	}

	test_it_should_be_possible_to_retrieve_leftmost_and_rightmost_cols_of_view {
		this.assertEqual(
			0,
			GRView.newDetached(2, 3).leftmostCol
		);
		this.assertEqual(
			1,
			GRView.newDetached(2, 3).rightmostCol
		);
	}

	test_it_should_be_possible_to_retrieve_topmost_and_bottommost_rows_of_view {
		this.assertEqual(
			0,
			GRView.newDetached(2, 3).topmostRow
		);
		this.assertEqual(
			2,
			GRView.newDetached(2, 3).bottommostRow
		);
	}

	// validations
	test_it_should_be_possible_to_determine_if_a_view_contains_a_specified_point {
		var view = GRView.newDetached(2, 3);

		this.assert( view.containsPoint(Point.new(0, 0)) );
		this.assert( view.containsPoint(Point.new(1, 2)) );

		this.assertEqual(false, view.containsPoint(Point.new(2, 2)) );
		this.assertEqual(false, view.containsPoint(Point.new(1, 3)) );
		this.assertEqual(false, view.containsPoint(Point.new(2, 3)) );

		this.assertEqual(false, view.containsPoint(Point.new(-1, 0)) );
		this.assertEqual(false, view.containsPoint(Point.new(0, -1)) );
		this.assertEqual(false, view.containsPoint(Point.new(-1, -1)) );

		this.assertNoErrorThrown { view.validateContainsPoint(Point.new(0, 0)) };
		this.assertNoErrorThrown { view.validateContainsPoint(Point.new(1, 2)) };

		this.assertErrorThrown(Error, { view.validateContainsPoint(Point.new(2, 2)) } );
		this.assertErrorThrown(Error, { view.validateContainsPoint(Point.new(1, 3)) } );
		this.assertErrorThrown(Error, { view.validateContainsPoint(Point.new(2, 3)) } );

		this.assertErrorThrown(Error, { view.validateContainsPoint(Point.new(-1, 0)) } );
		this.assertErrorThrown(Error, { view.validateContainsPoint(Point.new(0, -1)) } );
		this.assertErrorThrown(Error, { view.validateContainsPoint(Point.new(-1, -1)) } );
	}

	test_it_should_be_possible_to_determine_if_a_view_contains_a_specified_bounds {
		var view = GRView.newDetached(2, 3);

		this.assert( view.containsBounds(Point.new(0, 0), 2, 3) );
		this.assert( view.containsBounds(Point.new(0, 0), 2, 1) );
		this.assert( view.containsBounds(Point.new(1, 2), 1, 1) );

		this.assertEqual( false, view.containsBounds(Point.new(0, 0), 3, 3) );
		this.assertEqual( false, view.containsBounds(Point.new(1, 2), 1, 2) );
		this.assertEqual( false, view.containsBounds(Point.new(1, 2), 2, 1) );

		this.assertEqual( false, view.containsBounds(Point.new(-1, 0), 1, 1) );
		this.assertEqual( false, view.containsBounds(Point.new(-1, 0), 1, 1) );
		this.assertEqual( false, view.containsBounds(Point.new(-1, -1), 1, 1) );

		this.assertNoErrorThrown { view.validateContainsBounds(Point.new(0, 0), 2, 3) };
		this.assertNoErrorThrown { view.validateContainsBounds(Point.new(0, 0), 2, 1) };
		this.assertNoErrorThrown { view.validateContainsBounds(Point.new(1, 2), 1, 1) };

		this.assertErrorThrown(Error) { view.validateContainsBounds(Point.new(0, 0), 3, 3) };
		this.assertErrorThrown(Error) { view.validateContainsBounds(Point.new(1, 2), 1, 2) };
		this.assertErrorThrown(Error) { view.validateContainsBounds(Point.new(1, 2), 2, 1) };

		this.assertErrorThrown(Error) { view.validateContainsBounds(Point.new(-1, 0), 1, 1) };
		this.assertErrorThrown(Error) { view.validateContainsBounds(Point.new(0, -1), 1, 1) };
		this.assertErrorThrown(Error) { view.validateContainsBounds(Point.new(-1, -1), 1, 1) };
	}

	// action and value
	test_it_should_be_possible_to_get_notified_of_view_events_by_adding_actions_to_a_view {
		var view = GRView.newDetached(2, 2);
		var actionListener1 = MockActionListener.new(view);
		var actionListener2 = MockActionListener.new(view);

		view.action.value("hey, something happened");

		this.assert( actionListener1.hasBeenNotifiedOf( [ ["hey, something happened"] ] ) );
		this.assert( actionListener2.hasBeenNotifiedOf( [ ["hey, something happened"] ] ) );
	}

	test_it_should_be_possible_to_remove_added_actions_from_a_view {
		var view = GRView.newDetached(2, 2);
		var actionListener1 = MockActionListener.new(view);
		var actionListener2 = MockActionListener.new(view);

		actionListener1.removeListener;
		actionListener2.removeListener;

		this.assertEqual( nil, view.action );
	}

	test_an_action_should_no_longer_receive_notifications_once_it_has_been_removed_from_a_view {
		var view = GRView.newDetached(2, 2);
		var actionListener1 = MockActionListener.new(view);
		var actionListener2 = MockActionListener.new(view);

		actionListener1.removeListener;

		view.action.value("hey, something happened");

		this.assert( actionListener1.hasNotBeenNotifiedOfAnything );
		this.assert( actionListener2.hasBeenNotifiedOf( [ ["hey, something happened"] ] ) );
	}

	test_it_should_be_possible_to_set_a_views_value {
		var view = GRView.newDetached(2, 2);
		view.value = \xyz;
		this.assertEqual(\xyz, view.value);
	}

	test_when_a_views_value_is_set_to_a_new_value_the_view_should_be_refreshed {
		var view = GRView.newDetached(2, 2);
		var listener;
		view.id = \xyz;
		view.value = \abc;
		listener = MockViewLedRefreshedListener.new(view);

		view.value = \def;

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \xyz, point: Point.new(0, 0), on: false ),
					( source: \xyz, point: Point.new(1, 0), on: false ),
					( source: \xyz, point: Point.new(0, 1), on: false ),
					( source: \xyz, point: Point.new(1, 1), on: false )
				]
			)
		);
	}

	test_when_a_views_value_is_set_but_not_changed_the_view_should_not_be_refreshed {
		var view = GRView.newDetached(2, 2);
		var listener;
		view.value = \abc;
		listener = MockViewLedRefreshedListener.new(view);

		view.value = \abc;

		this.assert(listener.hasNotBeenNotifiedOfAnything);
	}

	test_when_a_views_value_is_set_to_a_new_value_using_value_action_the_view_should_be_refreshed_and_action_should_be_triggered {
		var view = GRView.newDetached(2, 2);
		var listener = MockViewLedRefreshedListener.new(view);
		var actionListener = MockActionListener.new(view);
		view.id = \abc;

		view.valueAction = \xyz;

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \abc, point: Point.new(0, 0), on: false ),
					( source: \abc, point: Point.new(1, 0), on: false ),
					( source: \abc, point: Point.new(0, 1), on: false ),
					( source: \abc, point: Point.new(1, 1), on: false )
				]
			)
		);
		this.assert( actionListener.hasBeenNotifiedOf( [ [view, \xyz] ] ) );
	}

	test_when_a_views_value_is_set_but_not_changed_using_value_action_the_view_should_not_be_refreshed_and_no_action_should_be_triggered {
		var view = GRView.newDetached(2, 2);
		var listener;
		var actionListener;
		view.value = \abc;
		listener = MockViewLedRefreshedListener.new(view);
		actionListener = MockActionListener.new(view);

		view.valueAction = \abc;

		this.assert(listener.hasNotBeenNotifiedOfAnything);
		this.assert(actionListener.hasNotBeenNotifiedOfAnything);
	}

	// button events and state
	test_it_should_be_possible_to_send_a_button_event_to_a_view_and_get_a_response_of_how_the_event_was_handled {
		var view = GRView.new;
		var response = view.press(Point.new(0, 0));
		this.assertEqual( [ ( view: view, point: Point.new(0, 0) ) ], response );
	}

	test_button_state_should_be_saved_for_each_view_and_should_be_updated_by_incoming_button_events {
		var view = GRView.new;
		view.press(Point.new(0, 0));
		this.assert( view.isPressedAt(Point.new(0, 0)) );

		view.release(Point.new(0, 0));
		this.assert( view.isReleasedAt(Point.new(0, 0)) );
	}

	test_all_buttons_of_a_view_should_be_released_on_creation {
		var view = GRView.new(nil, nil, 4, 4);
		this.assert( view.allReleased );
	}

	test_when_the_button_state_of_a_view_is_updated_by_incoming_button_events_the_views_view_button_state_changed_actions_should_be_triggered_with_notification_of_the_update {
		var view = GRView.newDetached(4, 4);
		var viewButtonStateChangedListener = MockViewButtonStateChangedListener.new(view);
		view.press(Point.new(1, 1));
		view.release(Point.new(1, 1));

		this.assert(
			viewButtonStateChangedListener.hasBeenNotifiedOf(
				[
					( point: Point.new(1, 1), pressed: true ),
					( point: Point.new(1, 1), pressed: false )
				]
			)
		);
	}

	test_a_disabled_view_should_not_respond_to_button_events {
		var view = GRView.newDisabled(nil, nil, 4, 4);
		var response = view.press(Point.new(0, 0));
		this.assertEqual([], response);
	}

	test_a_disabled_view_should_not_update_button_state_according_to_incoming_button_events {
		var view = GRView.newDisabled(nil, nil, 4, 4);
		view.press(Point.new(0, 0));
		this.assert(view.isReleasedAt(Point.new(0, 0)));
	}

	test_subsequent_button_events_of_the_same_type_and_on_the_same_button_should_be_ignored {
		var view = GRView.newDetached(4, 4);
		var response;
		var viewButtonStateChangedListener = MockViewButtonStateChangedListener.new(view);

		response = view.press(Point.new(1, 1));

		this.assertEqual(
			[
				(view: view, point: Point.new(1, 1))
			],
			response
		);
		this.assert( view.isPressedAt(Point.new(1, 1)) );
		this.assert(
			viewButtonStateChangedListener.hasBeenNotifiedOf(
				[
					( point: Point.new(1, 1), pressed: true )
				]
			)
		);

		response = view.press(Point.new(1, 1));

		this.assertEqual(
			[],
			response
		);
		this.assert( view.isPressedAt(Point.new(1, 1)) );
		this.assert(
			viewButtonStateChangedListener.hasBeenNotifiedOf(
				[
					( point: Point.new(1, 1), pressed: true )
				]
			)
		);

		response = view.release(Point.new(1, 1));

		this.assertEqual(
			[
				(view: view, point: Point.new(1, 1))
			],
			response
		);
		this.assert( view.isReleasedAt(Point.new(1, 1)) );
		this.assert(
			viewButtonStateChangedListener.hasBeenNotifiedOf(
				[
					( point: Point.new(1, 1), pressed: true ),
					( point: Point.new(1, 1), pressed: false )
				]
			)
		);

		response = view.release(Point.new(1, 1));

		this.assertEqual(
			[],
			response
		);
		this.assert( view.isReleasedAt(Point.new(1, 1)) );
		this.assert(
			viewButtonStateChangedListener.hasBeenNotifiedOf(
				[
					( point: Point.new(1, 1), pressed: true ),
					( point: Point.new(1, 1), pressed: false )
				]
			)
		);
	}

	test_it_should_be_possible_to_determine_how_many_buttons_on_a_view_are_pressed {
		var view = GRView.newDetached(4, 4);

		view.press(Point.new(0, 0));
		view.press(Point.new(1, 1));
		view.press(Point.new(2, 2));

		this.assertEqual(3, view.numPressed);
	}

	test_it_should_be_possible_to_determine_how_many_buttons_within_a_specified_part_of_a_view_are_pressed {
		var view = GRView.newDetached(4, 4);

		view.press(Point.new(0, 0));
		view.press(Point.new(1, 1));
		view.press(Point.new(2, 2));

		this.assertEqual(2, view.numPressedWithinBounds(Point.new(1, 1), 2, 2));
	}

	test_it_should_be_possible_to_determine_how_many_buttons_on_a_view_are_released {
		var view = GRView.newDetached(4, 4);

		view.press(Point.new(0, 0));
		view.press(Point.new(1, 1));
		view.press(Point.new(2, 2));

		this.assertEqual(13, view.numReleased);
	}

	test_it_should_be_possible_to_determine_how_many_buttons_within_a_specified_part_of_a_view_are_released {
		var view = GRView.newDetached(4, 4);

		view.press(Point.new(0, 0));
		view.press(Point.new(1, 1));
		view.press(Point.new(2, 2));

		this.assertEqual(2, view.numReleasedWithinBounds(Point.new(1, 1), 2, 2));
	}

	test_it_should_be_possible_to_determine_if_any_button_on_a_view_is_pressed {
		var view = GRView.newDetached(2, 2);

		this.assertEqual(false, view.anyPressed);

		view.press(Point.new(0, 0));

		this.assert(view.anyPressed);
	}

	test_it_should_be_possible_to_determine_if_any_button_within_a_specified_part_of_a_view_is_pressed {
		var view = GRView.newDetached(4, 4);

		view.press(Point.new(0, 0));

		this.assertEqual(false, view.anyPressedWithinBounds(Point.new(1, 1), 2, 2));

		view.press(Point.new(1, 1));

		this.assert(view.anyPressedWithinBounds(Point.new(1, 1), 2, 2));
	}

	test_it_should_be_possible_to_determine_if_all_buttons_on_a_view_are_pressed {
		var view = GRView.newDetached(2, 2);

		this.assertEqual(false, view.allPressed);

		view.asPoints.do { |point| view.press(point) };

		this.assert(view.allPressed);
	}

	test_it_should_be_possible_to_determine_if_all_buttons_within_a_specified_part_of_a_view_are_pressed {
		var view = GRView.newDetached(4, 4);

		view.press(Point.new(0, 0));

		this.assertEqual(false, view.allPressedWithinBounds(Point.new(1, 1), 2, 2));

		view.asPoints.do { |point| view.press(point) };

		this.assert(view.allPressedWithinBounds(Point.new(1, 1), 2, 2));
	}

	test_it_should_be_possible_to_determine_if_any_button_on_a_view_is_released {
		var view = GRView.newDetached(2, 2);

		this.assert(view.anyReleased);

		view.press(Point.new(0, 0));

		this.assert(view.anyReleased);

		view.press(Point.new(0, 1));
		view.press(Point.new(1, 0));
		view.press(Point.new(1, 1));

		this.assertEqual(false, view.anyReleased);
	}

	test_it_should_be_possible_to_determine_if_any_button_within_a_specified_part_of_a_view_is_released {
		var view = GRView.newDetached(4, 4);

		this.assert(view.anyReleasedWithinBounds(Point.new(1, 1), 2, 2));

		view.press(Point.new(1, 1));

		this.assert(view.anyReleasedWithinBounds(Point.new(1, 1), 2, 2));

		view.press(Point.new(1, 2));
		view.press(Point.new(2, 1));
		view.press(Point.new(2, 2));

		this.assertEqual(false, view.anyReleasedWithinBounds(Point.new(1, 1), 2, 2));
	}

	test_it_should_be_possible_to_determine_if_all_buttons_on_a_view_are_released {
		var view = GRView.newDetached(2, 2);

		this.assert(view.allReleased);

		view.press(Point.new(0, 0));

		this.assertEqual(false, view.allReleased);

		view.press(Point.new(0, 1));
		view.press(Point.new(1, 0));
		view.press(Point.new(1, 1));

		this.assertEqual(false, view.allReleased);
	}

	test_it_should_be_possible_to_determine_if_all_buttons_within_a_specified_part_of_a_view_are_released {
		var view = GRView.newDetached(4, 4);

		this.assert(view.allReleasedWithinBounds(Point.new(1, 1), 2, 2));

		view.press(Point.new(1, 1));

		this.assertEqual(false, view.allReleasedWithinBounds(Point.new(1, 1), 2, 2));

		view.press(Point.new(1, 2));
		view.press(Point.new(2, 1));
		view.press(Point.new(2, 2));

		this.assertEqual(false, view.allReleasedWithinBounds(Point.new(1, 1), 2, 2));
	}

	test_it_should_be_possible_to_determine_which_of_the_currently_pressed_buttons_on_a_view_was_pressed_first {
		var view = GRView.newDetached(4, 4);

		this.assertEqual(nil, view.firstPressed);

		view.press(Point.new(0, 0));

		this.assertEqual(Point.new(0, 0), view.firstPressed);

		view.press(Point.new(1, 1));
		view.press(Point.new(2, 2));
		view.release(Point.new(0, 0));

		this.assertEqual(Point.new(1, 1), view.firstPressed);
	}

	test_it_should_be_possible_to_determine_which_of_the_currently_pressed_buttons_on_a_view_was_pressed_last {
		var view = GRView.newDetached(4, 4);

		this.assertEqual(nil, view.lastPressed);

		view.press(Point.new(0, 0));

		this.assertEqual(Point.new(0, 0), view.lastPressed);

		view.press(Point.new(1, 1));
		view.press(Point.new(2, 2));
		view.press(Point.new(3, 3));
		view.release(Point.new(3, 3));

		this.assertEqual(Point.new(2, 2), view.lastPressed);
	}

	test_it_should_be_possible_to_determine_in_what_order_currently_pressed_buttons_have_been_pressed_on_a_view {
		var view = GRView.newDetached(4, 4);

		view.press(Point.new(3, 3));
		view.press(Point.new(0, 0));
		view.press(Point.new(1, 1));
		view.press(Point.new(2, 2));
		view.release(Point.new(1, 1));

		this.assertEqual(
			[
				Point.new(3, 3),
				Point.new(0, 0),
				Point.new(2, 2)
			],
			view.pointsPressed
		);
	}

	test_it_should_be_possible_to_determine_in_what_order_currently_pressed_buttons_within_a_specified_part_of_a_view_have_been_pressed {
		var view = GRView.newDetached(4, 4);

		view.press(Point.new(3, 3));
		view.press(Point.new(0, 0));
		view.press(Point.new(1, 1));
		view.press(Point.new(2, 2));
		view.release(Point.new(1, 1));

		this.assertEqual(
			[
				Point.new(3, 3),
				Point.new(2, 2)
			],
			view.pointsPressedWithinBounds(Point.new(2, 2), 2, 2)
		);
	}

	test_it_should_be_possible_to_determine_which_left_right_top_and_bottommost_buttons_are_pressed_on_a_view {
		var view = GRView.newDetached(4, 4);

		this.assertEqual([], view.leftmostPressed);
		this.assertEqual(nil, view.leftmostColPressed);
		this.assertEqual([], view.rightmostPressed);
		this.assertEqual(nil, view.rightmostColPressed);
		this.assertEqual([], view.topmostPressed);
		this.assertEqual(nil, view.topmostRowPressed);
		this.assertEqual([], view.bottommostPressed);
		this.assertEqual(nil, view.bottommostRowPressed);

		[ Point.new(1,1),
			Point.new(3,1),
			Point.new(3,3),
			Point.new(1,3) ].do { |point| view.press(point) };

		this.assertEqual(
			[ Point.new(1,1), Point.new(1,3) ],
			view.leftmostPressed
		);
		this.assertEqual(1, view.leftmostColPressed);

		this.assertEqual(
			[ Point.new(3,1), Point.new(3,3) ],
			view.rightmostPressed
		);
		this.assertEqual(3, view.rightmostColPressed);

		this.assertEqual(
			[ Point.new(1,1), Point.new(3,1) ],
			view.topmostPressed
		);
		this.assertEqual(1, view.topmostRowPressed);

		this.assertEqual(
			[ Point.new(3,3), Point.new(1,3) ],
			view.bottommostPressed
		);
		this.assertEqual(3, view.bottommostRowPressed);
	}

	test_when_a_view_is_disabled_all_pressed_buttons_of_view_should_be_released {
		var view = GRView.newDetached(4, 4);
		view.asPoints.do { |point| view.press(point) };
		view.disable;
		this.assert(view.allReleased)
	}

	// out of bounds errors
	test_an_out_of_bounds_button_state_check_should_throw_an_error {
		var view = GRView.newDetached(4, 4);
		this.assertErrorThrown( Error, { view.isPressedAt( Point.new(4, 4) ) } );
	}

	test_an_out_of_bounds_button_event_should_throw_an_error {
		var view = GRView.newDetached(2, 3);

		this.assertErrorThrown( Error, { view.press( Point.new(-1, 0) ) } );
		this.assertErrorThrown( Error, { view.press( Point.new(0, -1) ) } );
		this.assertErrorThrown( Error, { view.press( Point.new(2, 1) ) } );
		this.assertErrorThrown( Error, { view.press( Point.new(3, 1) ) } );
		this.assertErrorThrown( Error, { view.press( Point.new(1, 3) ) } );
		this.assertErrorThrown( Error, { view.press( Point.new(1, 4) ) } );
	}

	test_an_out_of_bounds_led_state_check_should_throw_an_error {
		var view = GRView.newDetached(4, 4);
		this.assertErrorThrown( Error, { view.isLitAt( Point.new(4, 4) ) } );
	}

	test_an_out_of_bounds_refresh_point_should_throw_an_error {
		var view = GRView.newDetached(4, 4);
		this.assertErrorThrown( Error, { view.refreshPoint( Point.new(4, 4) ) } );
	}

	// led state
	test_it_should_be_possible_to_check_whether_a_led_of_a_view_is_lit {
		var view = MockOddColsLitView.newDetached(4, 4);

		this.assert(view.isLitAt(Point.new(1, 0)));
	}

	test_it_should_be_possible_to_check_whether_any_led_of_a_view_is_lit {
		var view = MockOddColsLitView.newDetached(4, 4);

		this.assert(view.anyLit);
	}

	test_it_should_be_possible_to_check_whether_all_leds_of_a_view_are_lit {
		var view = MockLitView.newDetached(4, 4);

		this.assert(view.allLit);
	}

	test_it_should_be_possible_to_check_whether_a_led_of_a_view_is_unlit {
		var view = MockOddColsLitView.newDetached(4, 4);

		this.assert(view.isUnlitAt(Point.new(0, 0)));
	}

	test_it_should_be_possible_to_check_whether_any_led_of_a_view_is_unlit {
		var view = MockOddColsLitView.newDetached(4, 4);

		this.assert(view.anyUnlit);
	}

	test_it_should_be_possible_to_check_whether_all_leds_of_a_view_are_unlit {
		var view = MockUnlitView.newDetached(4, 4);

		this.assert(view.allUnlit);
	}

	// led events and refresh
	test_when_a_point_of_an_enabled_view_is_refreshed_the_views_view_led_refreshed_actions_should_get_notified_of_the_refreshed_led_and_its_state {
		var view = GRView.newDetached(4, 4);
		var listener = MockViewLedRefreshedListener.new(view);
		view.id = \abc;

		view.refreshPoint(Point.new(1, 1));
		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \abc, point: Point.new(1, 1), on: false )
				]
			)
		);
	}

	test_when_bounds_of_an_enabled_view_is_refreshed_the_views_view_led_refreshed_actions_should_get_notified_of_refreshed_leds_and_their_state {
		var view = GRView.newDetached(4, 4);
		var listener = MockViewLedRefreshedListener.new(view);
		view.id = \abc;

		view.refreshBounds(Point.new(2, 2), 2, 2);
		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \abc, point: Point.new(2, 2), on: false ),
					( source: \abc, point: Point.new(3, 2), on: false ),
					( source: \abc, point: Point.new(2, 3), on: false ),
					( source: \abc, point: Point.new(3, 3), on: false )
				]
			)
		);
	}

	test_when_an_entire_enabled_view_is_refreshed_the_views_view_led_refreshed_actions_should_get_notified_of_refreshed_leds_and_their_state {
		var view = GRView.newDetached(4, 4);
		var listener = MockViewLedRefreshedListener.new(view);
		view.id = \abc;

		view.refresh;
		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \abc, point: Point.new(0, 0), on: false ),
					( source: \abc, point: Point.new(1, 0), on: false ),
					( source: \abc, point: Point.new(2, 0), on: false ),
					( source: \abc, point: Point.new(3, 0), on: false ),
					( source: \abc, point: Point.new(0, 1), on: false ),
					( source: \abc, point: Point.new(1, 1), on: false ),
					( source: \abc, point: Point.new(2, 1), on: false ),
					( source: \abc, point: Point.new(3, 1), on: false ),
					( source: \abc, point: Point.new(0, 2), on: false ),
					( source: \abc, point: Point.new(1, 2), on: false ),
					( source: \abc, point: Point.new(2, 2), on: false ),
					( source: \abc, point: Point.new(3, 2), on: false ),
					( source: \abc, point: Point.new(0, 3), on: false ),
					( source: \abc, point: Point.new(1, 3), on: false ),
					( source: \abc, point: Point.new(2, 3), on: false ),
					( source: \abc, point: Point.new(3, 3), on: false )
				]
			)
		);
	}

	test_refreshing_a_disabled_view_should_throw_an_error {
		var view = GRView.newDisabled(nil, nil, 2, 2);

		this.assertErrorThrown(Error) { view.refresh };
		this.assertErrorThrown(Error) { view.refreshBounds(Point.new(1, 1), 1, 1) };
		this.assertErrorThrown(Error) { view.refreshPoint(Point.new(0, 0)) };
	}

	test_when_a_disabled_view_is_enabled_it_should_be_refreshed {
		var view = GRView.newDisabled(nil, nil, 2, 2);
		var listener = MockViewLedRefreshedListener.new(view);
		view.id = \abc;

		view.enable;

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \abc, point: Point.new(0, 0), on: false ),
					( source: \abc, point: Point.new(1, 0), on: false ),
					( source: \abc, point: Point.new(0, 1), on: false ),
					( source: \abc, point: Point.new(1, 1), on: false ),
				]
			)
		);
	}

	// string representations
	test_the_string_representation_of_a_view_should_include_id_bounds_and_whether_the_view_is_enabled {
		var view = GRView.newDetached(4, 3);
		this.assertEqual(
			"a GRView (4x3, enabled)",
			view.asString
		);
		view.id = \test;
		this.assertEqual(
			"a GRView (test, 4x3, enabled)",
			view.asString
		);
	}

	test_a_plot_of_a_view_should_describe_where_buttons_and_leds_currently_are_pressed_and_lit {
		var view = MockOddColsLitView.newDetached(4, 3);
		view.press(Point.new(1, 2));
		view.press(Point.new(3, 0));
		this.assertEqual(
			"  0 1 2 3      0 1 2 3\n" ++
			"0 - - - P    0 - L - L\n" ++
			"1 - - - -    1 - L - L\n" ++
			"2 - P - -    2 - L - L\n",
			view.asPlot
		);
	}

	test_a_tree_plot_of_a_view_should_describe_where_buttons_and_leds_currently_are_pressed_and_lit_and_also_include_its_string_representation {
		var view = GRView.newDetached(4, 3);
		this.assertEqual(
			"a GRView (4x3, enabled)\n" ++
			"  0 1 2 3      0 1 2 3\n" ++
			"0 - - - -    0 - - - -\n" ++
			"1 - - - -    1 - - - -\n" ++
			"2 - - - -    2 - - - -\n" ++
			"\n",
			view.asTree(true)
		);
	}
}
