GRContainerViewTests : Test {
	var
		mockTopContainer,
		mockChildContainer,
		mockView
	;

	setup {
		GRTestsHelper.saveGlobals;
		GRTestsHelper.disableTraceAndFlash;
		mockTopContainer = MockLitContainerView.newDetached(4, 4);
		mockTopContainer.id = \topContainer;
		mockChildContainer = GRContainerView.new(mockTopContainer, Point.new(1, 1), 3, 3);
		mockChildContainer.id = \childContainer;
		mockView = MockLitView.new(mockChildContainer, Point.new(1, 1), 2, 2);
		mockView.id = \view;
	}

	teardown {
		GRTestsHelper.restoreGlobals;
	}

	// test helpers
	assertContainerIsParentOfView { |container, view|
		this.assert( container.isParentOf(view) );
		this.assertEqual( container, view.parent );
		this.assert( view.hasParent );
		this.assertEqual( false, view.isDetached );
	}

	assertContainerIsNotParentOfView { |container, view|
		this.assertEqual( false, container.isParentOf(view) );
		this.assertEqual( nil, view.parent );
		this.assertEqual( false, view.hasParent );
		this.assert( view.isDetached );
	}

	// validations
	test_it_should_be_possible_to_determine_whether_a_view_would_contain_another_view_at_a_specific_origin {
		container = GRContainerView.newDetached(2, 3);
		view = GRView.newDetached(2, 2);
		this.assert(container.isWithinBounds(view, Point.new(0, 0)));
		this.assertEqual(false, container.isWithinBounds(view, Point.new(2, 2)));
		this.assertNoErrorThrown { container.validateWithinBounds(view, Point.new(0, 0)) };
		this.assertErrorThrown(Error) { container.validateWithinBounds(view, Point.new(2, 2)) };
	}

	test_it_should_be_possible_to_determine_whether_a_container_is_parent_of_a_view {
		container = GRContainerView.newDetached(2, 2);
		view1 = GRView.new(container, Point.new(0, 0), 2, 2);
		view2 = GRView.newDetached(2, 2);
		this.assertNoErrorThrown { container.validateParentOf(view1) };
		this.assertErrorThrown(Error) { container.validateParentOf(view2) };
	}

	test_if_a_container_has_no_children_it_should_be_considered_empty {
		container = GRContainerView.newDetached(2, 2);

		this.assert(container.isEmpty);
	}

	test_if_a_container_has_one_or_more_children_it_should_not_be_considered_empty {
		container = GRContainerView.newDetached(2, 2);
		GRView.new(container, Point.new(0, 0), 2, 2);

		this.assertEqual(false, container.isEmpty);
	}

	// parent - child
	test_it_should_be_possible_to_attach_a_child_view_to_a_container_view_on_creation_of_the_child_view {
		container = GRContainerView.newDetached(4, 4);
		view1 = GRView.new(container, Point.new(0, 0), 2, 2);
		view2 = GRView.new(container, Point.new(2, 2), 2, 2);

		this.assertContainerIsParentOfView(container, view1);
		this.assertEqual(Point.new(0, 0), view1.origin);
		this.assertContainerIsParentOfView(container, view2);
		this.assertEqual(Point.new(2, 2), view2.origin);
	}

	test_it_should_be_possible_to_attach_a_detached_view_as_a_child_to_a_container_view {
		container = GRContainerView.newDetached(4, 4);
		view1 = GRView.newDetached(2, 2);
		view2 = GRView.newDetached(2, 2);

		container.addChild(view1, Point.new(0, 0));
		container.addChild(view2, Point.new(2, 2));

		this.assertContainerIsParentOfView(container, view1);
		this.assertEqual(Point.new(0, 0), view1.origin);
		this.assertContainerIsParentOfView(container, view2);
		this.assertEqual(Point.new(2, 2), view2.origin);
	}

	test_it_should_be_possible_to_remove_child_views_from_a_container_view {
		container = GRContainerView.newDetached(4, 4);
		view1 = GRView.newDetached(2, 2);
		view2 = GRView.newDetached(2, 2);
		container.addChild(view1, Point.new(0, 0));
		container.addChild(view2, Point.new(2, 2));

		container.removeChild(view1);
		view2.remove;

		this.assertContainerIsNotParentOfView(container, view1);
		this.assertContainerIsNotParentOfView(container, view2);
	}

	test_it_should_be_possible_to_remove_all_child_views_of_a_container_view {
		container = GRContainerView.newDetached(4, 4);
		GRView.new(container, Point.new(0, 0), 2, 2);
		GRView.new(container, Point.new(2, 0), 2, 2);
		GRView.new(container, Point.new(2, 2), 2, 2);
		GRView.new(container, Point.new(0, 2), 2, 2);

		container.removeAllChildren;

		this.assert(container.isEmpty);
	}

	test_a_detached_view_should_not_have_a_parent {
		this.assertEqual(nil, GRView.newDetached(4, 4).parent);
	}

	test_a_detached_view_should_not_have_an_origin {
		this.assertEqual(nil, GRView.newDetached(4, 4).origin);
	}

	test_both_parent_and_origin_should_be_required_in_order_to_attach_a_child_view_to_a_container_view_on_creation_of_the_child_view {
		this.assertErrorThrown(Error) { GRView.new(nil, Point.new(0, 0)) };
		this.assertErrorThrown(Error) { GRView.new(GRView.newDetached, nil) };
	}

	test_an_origin_should_be_required_when_attaching_a_detached_view_to_a_container_view {
		container = GRContainerView.newDetached(4, 4);
		view = GRView.newDetached(2, 2);
		this.assertErrorThrown(Error) { container.addChild(view, nil) };
	}

	test_trying_to_remove_a_detached_view_should_throw_an_error {
		this.assertErrorThrown(Error) { GRView.newDetached(4, 4).remove };
	}

	test_it_should_be_possible_to_determine_whether_any_enabled_or_disabled_child_views_cover_a_specific_point {
		container = GRContainerView.newDetached(4, 4);
		GRView.new(container, Point.new(1, 1), 2, 2);
		GRView.newDisabled(container, Point.new(2, 2), 2, 2);

		this.assert(container.hasChildAt(Point.new(2, 2)));
		this.assertEqual( false, container.hasChildAt(Point.new(0, 3)) );
	}

	test_it_should_be_possible_to_retrieve_all_enabled_and_disabled_child_views_covering_a_specific_point {
		container = GRContainerView.newDetached(4, 4);
		view1 = GRView.new(container, Point.new(1, 1), 2, 2);
		view2 = GRView.newDisabled(container, Point.new(2, 2), 2, 2);

		this.assertEqual( [ view1 ], container.getChildrenAt(Point.new(1, 1)) );
		this.assertEqual( [ view1, view2 ], container.getChildrenAt(Point.new(2, 2)) );
		this.assertEqual( [], container.getChildrenAt(Point.new(0, 1)) );
	}

	test_it_should_be_possible_to_determine_if_any_enabled_child_views_cover_a_specific_point {
		container = GRContainerView.newDetached(4, 4);
		GRView.new(container, Point.new(1, 1), 2, 2);
		GRView.newDisabled(container, Point.new(2, 2), 2, 2);

		this.assert(container.hasAnyEnabledChildAt(Point.new(2, 2)));
		this.assertEqual( false, container.hasAnyEnabledChildAt(Point.new(3, 3)) );
		this.assertEqual( false, container.hasAnyEnabledChildAt(Point.new(0, 3)) );
	}

	test_it_should_be_possible_to_retrieve_the_topmost_enabled_child_view_covering_a_specific_point {
		container = GRContainerView.newDetached(4, 4);
		view1 = GRView.new(container, Point.new(1, 1), 2, 2);
		GRView.newDisabled(container, Point.new(2, 2), 2, 2);

		this.assertEqual( view1, container.getTopmostEnabledChildAt(Point.new(2, 2)) );
		this.assertEqual( nil, container.getTopmostEnabledChildAt(Point.new(0, 1)) );
	}

	test_when_an_enabled_child_view_is_added_to_a_container_that_has_buttons_pressed_on_child_views_bounds_the_buttons_should_be_released_on_the_container_before_the_child_view_is_added {
		container = GRContainerView.newDetached(4, 4);
		container.press(Point.new(0, 0));
		container.press(Point.new(1, 1));
		container.press(Point.new(2, 2));
		container.press(Point.new(3, 3));

		GRView.new(container, Point.new(1, 1), 2, 2);

		this.assert(container.isPressedAt(Point.new(0, 0)));
		this.assert(container.isReleasedAt(Point.new(1, 1)));
		this.assert(container.isReleasedAt(Point.new(2, 2)));
		this.assert(container.isPressedAt(Point.new(3, 3)));
	}

	test_it_should_be_possible_to_retrieve_all_parents_of_a_child_view {
		container1 = GRContainerView.newDetached(4, 4);
		container2 = GRContainerView.new(container1, Point.new(0,0), 4, 4);
		container3 = GRContainerView.new(container2, Point.new(0,0), 4, 4);
		view = GRView.new(container3, Point.new(0,0), 4, 4);

		this.assertEqual(
			[container3, container2, container1],
			view.get_parents
		);
	}

	test_it_should_not_be_possible_to_add_a_view_as_a_child_to_a_container_if_the_view_already_has_a_parent {
		container1 = GRContainerView.newDetached(4, 4);
		container2 = GRContainerView.newDetached(4, 4);
		view = GRView.new(container1, Point.new(0, 0), 2, 2);
		this.assertErrorThrown(Error) { container2.addChild(view, Point.new(0, 0)) };
	}

	test_it_should_not_be_possible_to_add_a_child_view_at_a_negative_origin {
		container = GRContainerView.newDetached(4, 4);
		view = GRView.newDetached(2, 2);
		this.assertErrorThrown(Error) { container.addChild(view, Point.new(-1, 1)) };
		this.assertErrorThrown(Error) { container.addChild(view, Point.new(1, -1)) };
		this.assertErrorThrown(Error) { container.addChild(view, Point.new(-1, -1)) };
	}

	test_when_a_child_view_is_enabled_on_a_container_that_have_buttons_pressed_on_the_child_views_bounds_the_buttons_should_be_released_on_the_container_before_the_child_view_is_enabled {
		container = GRContainerView.newDetached(4, 4);
		view = GRView.newDisabled(container, Point.new(1, 1), 2, 2);
		container.press(Point.new(0, 0));
		container.press(Point.new(1, 1));
		container.press(Point.new(2, 2));
		container.press(Point.new(3, 3));

		view.enable;

		this.assert(container.isPressedAt(Point.new(0, 0)));
		this.assert(container.isReleasedAt(Point.new(1, 1)));
		this.assert(container.isReleasedAt(Point.new(2, 2)));
		this.assert(container.isPressedAt(Point.new(3, 3)));
	}

	// button events and state
	test_a_containers_incoming_button_events_should_be_forwarded_to_any_enabled_child_view_that_cover_the_affected_button {
		topContainer = GRContainerView.newDetached(4, 4);
		childContainer = GRContainerView.new(topContainer, Point.new(1, 1), 3, 3);
		view = GRContainerView.new(childContainer, Point.new(1, 1), 2, 2);

		this.assertEqual(
			[
				( view: view, point: Point.new(0, 0) )
			],
			topContainer.press(Point.new(2, 2))
		);
		this.assert(view.isPressedAt(Point.new(0, 0)));
	}

	test_a_containers_incoming_button_events_should_not_be_forwarded_to_any_disabled_child_views_that_cover_the_affected_button {
		container = GRContainerView.newDetached(4, 4);
		view = GRView.new(container, Point.new(0,0), 4, 4);

		view.disable;

		this.assertEqual(
			[
				( view: container, point: Point.new(0, 0) )
			],
			container.press(Point.new(0, 0))
		);
	}

	test_when_incoming_button_events_are_forwarded_by_non_press_through_containers_they_should_not_be_handled_on_the_container {
		topContainer = GRContainerView.newDetached(4, 4, true, false);
		childContainer = GRContainerView.new(topContainer, Point.new(1, 1), 3, 3, true, false);
		view = GRContainerView.new(childContainer, Point.new(1, 1), 2, 2);

		this.assertEqual(
			[
				( view: view, point: Point.new(0, 0) )
			],
			topContainer.press(Point.new(2, 2))
		);
		this.assert(topContainer.isReleasedAt(Point.new(2, 2)));
		this.assert(childContainer.isReleasedAt(Point.new(1, 1)));
		this.assert(view.isPressedAt(Point.new(0, 0)));
	}

	test_when_incoming_button_events_are_forwarded_by_press_through_containers_they_should_also_be_handled_on_the_container {
		topContainer = GRContainerView.newDetached(8, 8, true, true);
		childContainer = GRContainerView.new(topContainer, Point.new(1, 1), 3, 3, true, true);
		view = GRView.new(childContainer, Point.new(1, 1), 2, 2);

		this.assertEqual(
			[
				( view: view, point: Point.new(0, 0) ),
				( view: childContainer, point: Point.new(1, 1) ),
				( view: topContainer, point: Point.new(2, 2) )
			],
			topContainer.press(Point.new(2, 2))
		);
		this.assert(topContainer.isPressedAt(Point.new(2, 2)));
		this.assert(childContainer.isPressedAt(Point.new(1, 1)));
		this.assert(view.isPressedAt(Point.new(0, 0)));
	}

	test_when_a_non_press_through_container_view_is_disabled_all_its_pressed_buttons_and_all_its_enabled_childrens_pressed_buttons_should_be_released {
		topContainer = GRContainerView.newDetached(8, 8);
		childContainer = GRContainerView.new(topContainer, Point.new(0, 0), 8, 8);
		view1 = GRView.new(childContainer, Point.new(0, 0), 2, 2);
		view2 = GRView.new(childContainer, Point.new(2, 2), 2, 2);
		topContainer.asPoints.each { |point| topContainer.press(point) };

		topContainer.disable;

		this.assert(topContainer.all_released?);
		this.assert(childContainer.all_released?);
		this.assert(view1.all_released?);
		this.assert(view2.all_released?);
	}

	test_when_a_press_through_container_view_is_disabled_all_its_pressed_buttons_and_all_its_enabled_childrens_pressed_buttons_should_be_released {
		topContainer = GRContainerView.newDetached(8, 8, true, true);
		childContainer = GRContainerView.new(topContainer, Point.new(0, 0), 8, 8, true, true);
		view1 = GRView.new(childContainer, Point.new(0, 0), 2, 2);
		view2 = GRView.new(childContainer, Point.new(2, 2), 2, 2);
		topContainer.asPoints.each { |point| topContainer.press(point) };

		topContainer.disable;

		this.assert(topContainer.all_released?);
		this.assert(childContainer.all_released?);
		this.assert(view1.all_released?);
		this.assert(view2.all_released?);
	}

	// led events and refresh
	test_if_a_point_of_a_container_is_refreshed_and_an_enabled_child_view_cover_the_point_the_child_view_led_state_should_override_container_led_state {
		listener = MockViewLedRefreshedListener.new(mockTopContainer);

		mockTopContainer.refresh_point(Point.new(0, 0));

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \topContainer, point: Point.new(0, 0), on: true )

			)
		);

		listener.reset_notifications;

		mockTopContainer.refresh_point(Point.new(1, 1));

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \childContainer, point: Point.new(1, 1), on: false )
				]
			)
		);

		listener.reset_notifications;

		mockTopContainer.refresh_point(Point.new(2, 2));

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \view, point: Point.new(2, 2), on: true )
				]
			)
		);
	}

	test_when_an_area_of_a_container_is_refreshed_on_the_points_where_enabled_child_views_are_the_child_view_led_state_should_override_container_led_state {
		listener = MockViewLedRefreshedListener.new(mockTopContainer);

		mockTopContainer.refresh_bounds(Point.new(1, 1), 3, 2);

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \childContainer, point: Point.new(1, 1), on: false ),
					( source: \childContainer, point: Point.new(2, 1), on: false ),
					( source: \childContainer, point: Point.new(3, 1), on: false ),
					( source: \childContainer, point: Point.new(1, 2), on: false ),
					( source: \view, point: Point.new(2, 2), on: true ),
					( source: \view, point: Point.new(3, 2), on: true ),
				]
			)
		);
	}

	test_when_an_entire_container_is_refreshed_on_the_points_where_enabled_child_views_are_the_child_view_led_state_should_override_container_led_state {
		listener = MockViewLedRefreshedListener.new(mockTopContainer);

		mockTopContainer.refresh;

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \topContainer, point: Point.new(0, 0), on: true ),
					( source: \topContainer, point: Point.new(1, 0), on: true ),
					( source: \topContainer, point: Point.new(2, 0), on: true ),
					( source: \topContainer, point: Point.new(3, 0), on: true ),
					( source: \topContainer, point: Point.new(0, 1), on: true ),
					( source: \childContainer, point: Point.new(1, 1), on: false ),
					( source: \childContainer, point: Point.new(2, 1), on: false ),
					( source: \childContainer, point: Point.new(3, 1), on: false ),
					( source: \topContainer, point: Point.new(0, 2), on: true ),
					( source: \childContainer, point: Point.new(1, 2), on: false ),
					( source: \view, point: Point.new(2, 2), on: true ),
					( source: \view, point: Point.new(3, 2), on: true ),
					( source: \topContainer, point: Point.new(0, 3), on: true ),
					( source: \childContainer, point: Point.new(1, 3), on: false ),
					( source: \view, point: Point.new(2, 3), on: true ),
					( source: \view, point: Point.new(3, 3), on: true )
				]
			)
		);
	}

	test_when_an_enabled_view_that_has_a_parent_is_refreshed_led_state_should_automatically_be_forwarded_to_the_parent {
		listener = MockViewLedRefreshedListener.new(mockTopContainer);

		mockView.refresh;

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \view, point: Point.new(2, 2), on: true ),
					( source: \view, point: Point.new(3, 2), on: true ),
					( source: \view, point: Point.new(2, 3), on: true ),
					( source: \view, point: Point.new(3, 3), on: true )
				]
			)
		);
	}

	test_when_an_enabled_view_that_has_a_disabled_parent_is_refreshed_led_state_should_not_be_forwarded_to_the_parent {
		container = GRContainerView.newDetached(4, 4);
		view = GRView.new(container, Point.new(0,0), 4, 4);
		container.disable;

		listener = MockViewLedRefreshedListener.new(container);

		view.refresh_point(Point.new(0, 0));
		view.refresh_bounds(Point.new(1, 1), 1, 1);
		view.refresh;

		this.assert(listener.hasNotBeenNotifiedOfAnything);
	}

	test_it_should_be_possible_to_refresh_only_the_points_of_a_container_where_led_state_is_not_overridden_by_any_child_view {
		listener = MockViewLedRefreshedListener.new(mockTopContainer);

		mockTopContainer.refresh(false);

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \topContainer, point: Point.new(0, 0), on: true ),
					( source: \topContainer, point: Point.new(1, 0), on: true ),
					( source: \topContainer, point: Point.new(2, 0), on: true ),
					( source: \topContainer, point: Point.new(3, 0), on: true ),
					( source: \topContainer, point: Point.new(0, 1), on: true ),
					( source: \topContainer, point: Point.new(0, 2), on: true ),
					( source: \topContainer, point: Point.new(0, 3), on: true )
				]
			)
		);
	}

	test_when_a_child_view_is_added_it_should_automatically_be_refreshed {
		container = GRContainerView.newDetached(4, 4);
		container.id = \container;
		view = GRView.newDetached(2, 2);
		view.id = \view;

		listener = MockViewLedRefreshedListener.new(container);

		container.addChild(view, Point.new(1, 1));

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \view, point: Point.new(1, 1), on: false ),
					( source: \view, point: Point.new(2, 1), on: false ),
					( source: \view, point: Point.new(1, 2), on: false ),
					( source: \view, point: Point.new(2, 2), on: false )
				]
			)
		);
	}

	test_when_a_child_view_is_disabled_its_bounds_on_parent_should_automatically_be_refreshed {
		container = GRContainerView.newDetached(4, 4);
		container.id = \container;
		view = GRView.new(container, Point.new(1, 1), 2, 2);
		view.id = \view;

		listener = MockViewLedRefreshedListener.new(container);

		view.disable;

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \container, point: Point.new(1, 1), on: false ),
					( source: \container, point: Point.new(2, 1), on: false ),
					( source: \container, point: Point.new(1, 2), on: false ),
					( source: \container, point: Point.new(2, 2), on: false )
				]
			)
		);
	}

	test_when_an_enabled_child_view_is_removed_its_bounds_on_parent_should_automatically_be_refreshed {
		container = GRContainerView.newDetached(4, 4);
		container.id = \container;
		view = GRView.new(container, Point.new(1, 1), 2, 2);
		view.id = \view;

		listener = MockViewLedRefreshedListener.new(container);

		container.removeChild(view);

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \container, point: Point.new(1, 1), on: false ),
					( source: \container, point: Point.new(2, 1), on: false ),
					( source: \container, point: Point.new(1, 2), on: false ),
					( source: \container, point: Point.new(2, 2), on: false )
				]
			)
		);
	}

	test_when_a_disabled_child_view_is_removed_its_bounds_on_parent_should_not_be_refreshed {
		container = GRContainerView.newDetached(4, 4);
		container.id = \container;
		view = GRView.newDisabled(container, Point.new(1, 1), 2, 2);
		view.id = \view;

		listener = MockViewLedRefreshedListener.new(container);

		container.removeChild(view);

		this.assert(listener.hasNotBeenNotifiedOfAnything);
	}

	// string representations
	test_the_string_representation_of_a_container_view_should_include_id_bounds_and_whether_the_view_is_enabled {
		container = GRContainerView.newDetached(4, 3);
		this.assertEqual(
			"a GRContainerView (4x3, enabled)",
			container.asString
		);
		container.id = :test;
		this.assertEqual(
			"a GRContainerView (test, 4x3, enabled)",
			container.asString
		);
	}

	test_plot_should_indicate_enabled_children_of_a_view_and_where_the_view_currently_is_pressed_and_lit {
		container = MockOddColsLitContainerView.newDetached(4, 3);
		view = GRView.new(container, Point.new(1, 1), 2, 2);
		this.assertEqual(
			"   0   1   2   3        0   1   2   3 \n" +
			"0  -   -   -   -     0  -   L   -   L \n" +
			"1  -  [-] [-]  -     1  -  [-] [-]  L \n" +
			"2  -  [-] [-]  -     2  -  [-] [-]  L \n",
			container.asPlot
		);
		container.press(Point.new(0, 0));
		view.press(Point.new(0, 1));
		this.assertEqual(
			"   0   1   2   3        0   1   2   3 \n" +
			"0  P   -   -   -     0  -   L   -   L \n" +
			"1  -  [-] [-]  -     1  -  [-] [-]  L \n" +
			"2  -  [-] [-]  -     2  -  [-] [-]  L \n",
			container.asPlot
		);
		view.disable;
		this.assertEqual(
			"   0   1   2   3        0   1   2   3 \n" +
			"0  P   -   -   -     0  -   L   -   L \n" +
			"1  -   -   -   -     1  -   L   -   L \n" +
			"2  -   -   -   -     2  -   L   -   L \n",
			container.asPlot
		);
	}

	test_a_tree_plot_of_a_view_should_indicate_where_buttons_and_leds_are_currently_pressed_and_lit_and_also_include_its_string_representation_and_also_recursively_print_its_childrens_tree_plots {
		topContainer = GRContainerView.newDetached(4, 3);
		container2 = GRContainerView.new(topContainer, Point.new(2, 1), 2, 2);
		GRView.new(container2, Point.new(0, 0), 2, 1);
		GRView.new(topContainer, Point.new(0, 0), 2, 1);
		this.assertEqual(
			"a GRContainerView (4x3, enabled)\n" +
			"   0   1   2   3        0   1   2   3 \n" +
			"0 [-] [-]  -   -     0 [-] [-]  -   - \n" +
			"1  -   -  [-] [-]    1  -   -  [-] [-]\n" +
			"2  -   -  [-] [-]    2  -   -  [-] [-]\n" +
			"\n" +
			"\ta GRContainerView (2x2, enabled)\n" +
			"\t   0   1        0   1 \n" +
			"\t0 [-] [-]    0 [-] [-]\n" +
			"\t1  -   -     1  -   - \n" +
			"\n" +
			"\t\ta GRView (2x1, enabled)\n" +
			"\t\t  0 1      0 1\n" +
			"\t\t0 - -    0 - -\n" +
			"\n" +
			"\ta GRView (2x1, enabled)\n" +
			"\t  0 1      0 1\n" +
			"\t0 - -    0 - -\n" +
			"\n",
			topContainer.asTree(true)
		);
	}

	// subclassing
	test_it_should_be_possible_to_create_a_subclass_of_container_that_do_not_indicate_enabled_children_in_plot {
		subclass = MockContainerViewSubclassThatActsAsAView.newDetached(4, 3);

		subclass.press(Point.new(1, 2));
		subclass.press(Point.new(3, 0));

		this.assertEqual(
			"  0 1 2 3      0 1 2 3\n" +
			"0 - - - P    0 - - - L\n" +
			"1 - - - -    1 - - - -\n" +
			"2 - P - -    2 - - - -\n",
			subclass.asPlot
		);
	}

	test_it_should_be_possible_to_create_a_subclass_of_container_that_do_not_recursively_plot_children {
		subclass = MockContainerViewSubclassThatActsAsAView.newDetached(4, 3);

		this.assertEqual(
			"a MockContainerViewSubclassThatActsAsAView (4x3, enabled)\n" +
			"  0 1 2 3      0 1 2 3\n" +
			"0 - - - -    0 - - - -\n" +
			"1 - - - -    1 - - - -\n" +
			"2 - - - -    2 - - - -\n" +
			"\n",
			subclass.asTree(true)
		);
	}

	test_it_should_be_possible_to_create_a_subclass_of_container_that_do_not_allow_addition_and_removal_of_children {
		subclass = MockContainerViewSubclassThatActsAsAView.newDetached(8, 8);

		this.assertErrorThrown(Error) {
			subclass.addChild(GRView.newDetached(4, 4), Point.new(0, 0))
		};
	}

	// view switching
	test_it_should_be_possible_to_switch_between_views_by_index {
		container = GRContainerView.newDetached(4, 4);
		GRView.new(container, Point.new(0, 0), 4, 4);
		child2 = GRView.newDisabled(container, Point.new(0, 0), 4, 4);

		container.swithToChildByIndex(1);

		this.assertEqual([child2], container.enabledChildren);
	}

/*
	test_defaults {
		this.assertEqual(false, a3x3GridCompositeView.lit);
	}

	test_when_gridcomposite_set_lit_leds_should_refresh {
		this.addTestLedEventListener(a2x2GridCompositeView);

		a2x2GridCompositeView.lit = true;

		this.assertEqual(
			[
				( source: \a2x2GridCompositeView, point: Point.new(0, 0), on: true ),
				( source: \a2x2GridCompositeView, point: Point.new(1, 0), on: true ),
				( source: \a2x2GridCompositeView, point: Point.new(0, 1), on: true ),
				( source: \a2x2GridCompositeView, point: Point.new(1, 1), on: true ),
			],
			gotLed
		);
	}

	test_when_lit_gridcomposite_set_lit_again_leds_should_not_refresh {
		a2x2GridCompositeView.lit = true;

		this.addTestLedEventListener(a2x2GridCompositeView);

		a2x2GridCompositeView.lit = true;

		this.assertEqual(
			[],
			gotLed
		);
	}

	test_when_gridcomposite_with_enabled_child_set_lit_all_leds_except_child_led_should_refresh {
		GRView.new(a3x3GridCompositeView, Point.new(1, 1), 2, 2);

		this.addTestLedEventListener(a3x3GridCompositeView);

		a3x3GridCompositeView.lit = true;

		this.assertEqual(
			[
				( source: \a3x3GridCompositeView, point: Point.new(0, 0), on: true ),
				( source: \a3x3GridCompositeView, point: Point.new(1, 0), on: true ),
				( source: \a3x3GridCompositeView, point: Point.new(2, 0), on: true ),
				( source: \a3x3GridCompositeView, point: Point.new(0, 1), on: true ),
				( source: \a3x3GridCompositeView, point: Point.new(0, 2), on: true ),
			],
			gotLed
		);
	}

	test_when_gridcomposite_with_disabled_child_set_lit_all_leds_should_refresh {
		GRView.newDisabled(a3x3GridCompositeView, Point.new(1, 1), 2, 2);

		this.addTestLedEventListener(a3x3GridCompositeView);

		a3x3GridCompositeView.lit = true;

		this.assertEqual(
			[
				( source: \a3x3GridCompositeView, point: Point.new(0, 0), on: true ),
				( source: \a3x3GridCompositeView, point: Point.new(1, 0), on: true ),
				( source: \a3x3GridCompositeView, point: Point.new(2, 0), on: true ),
				( source: \a3x3GridCompositeView, point: Point.new(0, 1), on: true ),
				( source: \a3x3GridCompositeView, point: Point.new(1, 1), on: true ),
				( source: \a3x3GridCompositeView, point: Point.new(2, 1), on: true ),
				( source: \a3x3GridCompositeView, point: Point.new(0, 2), on: true ),
				( source: \a3x3GridCompositeView, point: Point.new(1, 2), on: true ),
				( source: \a3x3GridCompositeView, point: Point.new(2, 2), on: true ),
			],
			gotLed
		);
	}
*/
}

/*
GRContainerViewTests : Test {
	var
		gotLed
	;

	setup {
		GRTestsHelper.saveGlobals;
		GRTestsHelper.disableTraceAndFlash;
		gotLed = Array.new;
	}

	teardown {
		GRTestsHelper.restoreGlobals;
	}

	addTestLedEventListener { |view|
		var listener;
		listener = { |source, point, on|
			gotLed = gotLed.add( ( source: source.id, point: point, on: on ) );
		};
		view.addLedEventListener(listener);
		^listener
	}

	// Initialization
	test_if_parent_and_origin_is_supplied_to_a_gridview_it_should_be_added_to_parent {
		var container, view;
		container = GRContainerView.newDetached(4, 4);
		view = GRView.new(container, Point.new(0, 0), 2, 2);
		this.assert( container isParentOf: view );
	}

	// Validations
	test_within_bounds_validation {
		var container, view;
		container = GRContainerView.newDetached(2, 3);
		view = GRView.newDetached(2, 2);
		this.assert(container.withinBounds(view, Point.new(0, 0)));
		this.assertEqual(false, container.withinBounds(view, Point.new(2, 2)));
		this.assertNoErrorThrown { container.validateWithinBounds(view, Point.new(0, 0)) };
		this.assertErrorThrown(Error) { container.validateWithinBounds(view, Point.new(2, 2)) };
	}

	test_validation_of_overlap_with_enabled_children {
		var container, view;
		container = GRContainerView.newDetached(8, 8);
		GRView.new(container, Point.new(4, 4), 2, 2);
		view = GRView.newDetached(4, 4);
		this.assertEqual( false, container.overlapsWithEnabledChildren(view, Point.new(0, 0)) );
		this.assert( container.overlapsWithEnabledChildren(view, Point.new(2, 2)) );
		this.assertNoErrorThrown { container.validateDoesNotOverlapWithEnabledChildren(view, Point.new(0, 0)) };
		this.assertErrorThrown(Error) { container.validateDoesNotOverlapWithEnabledChildren(view, Point.new(2, 2)) }
	}

	test_validation_of_buttons_pressed_at_bounds_of_view {
		var container, view;
		container = GRContainerView.newDetached(8, 8);
		view = GRView.newDetached(4, 4);
		this.assertEqual( false, container.anyButtonPressedAtBoundsOf(view, Point.new(0, 0)) );
		this.assertNoErrorThrown { container.validateNoButtonsPressedAtBoundsOf(view, Point.new(0, 0)) };
		container.press(Point.new(2, 2));
		this.assert( container.anyButtonPressedAtBoundsOf(view, Point.new(0, 0)) );
		this.assertErrorThrown(Error) { container.validateNoButtonsPressedAtBoundsOf(view, Point.new(0, 0)) };
	}

	// Enable / Disable
	test_a_child_view_may_not_be_enabled_if_any_button_is_pressed_in_its_bounds_on_parent {
		var container, view;
		container = GRContainerView.newDetached(4, 4);
		view = GRView.newDisabled(container, Point.new(1, 1), 2, 2);
		container.press(Point.new(2, 2));
		this.assertErrorThrown(Error) { view.enable };
		container.release(Point.new(2, 2));
		this.assertNoErrorThrown { view.enable };
	}

	test_to_enable_a_child_view_it_must_not_overlap_with_other_enabled_childs_views {
		var container, view1, view2;
		container = GRContainerView.newDetached(4, 4);
		view1 = GRView.new(container, Point.new(0, 0), 2, 2);
		view2 = GRView.newDisabled(container, Point.new(1, 1), 3, 3);
		this.assertErrorThrown(Error) { view2.enable };
		view1.disable;
		this.assertNoErrorThrown { view2.enable };
	}

	test_disabling_a_container_view_releases_its_enabled_childrens_pressed_buttons {
		var container1, container2, view1, view2;
		container1 = GRContainerView.newDetached(8, 8);
		container2 = GRContainerView.new(container1, Point.new(0, 0), 8, 8);
		view1 = GRView.new(container2, Point.new(0, 0), 2, 2);
		view2 = GRView.new(container2, Point.new(2, 2), 2, 2);

		container1.press(Point.new(7, 7)); // on container2
		container1.press(Point.new(0, 0)); // on view1
		container1.press(Point.new(2, 2)); // on view2

		this.assert( container2.anyPressed );
		this.assert( view1.anyPressed );
		this.assert( view2.anyPressed );

		container2.disable;

		this.assert( container2.anyReleased );
		this.assert( view1.anyReleased );
		this.assert( view2.anyReleased );
	}

	test_a_disabled_view_should_not_respond_to_press_release_messages {
		var container, view;
		container = GRContainerView.newDetached(4, 4);
		view = GRView.new(container, Point.new(0, 0), 4, 4);
		view.disable;
		this.assertEqual(
			[
				(view: container, point: Point.new(0, 0))
			],
			container.press(Point.new(0, 0))
		);
	}

	test_when_a_child_view_is_disabled_its_bounds_on_parent_should_automatically_be_refreshed {
		var container, view;
		container = GRContainerView.newDetached(4, 4);
		container.id = \container;
		view = GRView.new(container, Point.new(1, 1), 2, 2);
		view.id = \view;

		this.addTestLedEventListener(container);

		view.disable;

		this.assertEqual(
			[
				( source: \container, point: Point.new(1, 1), on: false ),
				( source: \container, point: Point.new(2, 1), on: false ),
				( source: \container, point: Point.new(1, 2), on: false ),
				( source: \container, point: Point.new(2, 2), on: false )
			],
			gotLed
		);
	}

	test_a_disabled_child_view_should_not_refresh_thru_its_paren {
		var container, view;

		container = GRContainerView.newDetached(4, 4);
		view = GRView.new(container, Point.new(0,0), 4, 4);
		view.disable;

		this.addTestLedEventListener(container);

		view.refreshPoint(Point.new(0, 0));
		view.refreshBounds(Point.new(1, 1), 1, 1);
		view.refresh;

		this.assertEqual([], gotLed);
	}

	// Parent - Child
	test_it_should_be_possible_to_add_child_views_to_a_container_1 {
		var
			container = GRContainerView.newDetached(4, 4),
			view1 = GRView.newDetached(2, 2),
			view2 = GRView.newDetached(2, 2)
		;

		container.addChild(view1, Point.new(0, 0));
		container.addChild(view2, Point.new(2, 2));

		this.assertContainerIsParentOfView(container, view1);
		this.assertContainerIsParentOfView(container, view2);
	}

	test_it_should_be_possible_to_remove_child_views_from_a_container_1 {
		var
			container = GRContainerView.newDetached(4, 4),
			view1 = GRView.newDetached(2, 2),
			view2 = GRView.newDetached(2, 2)
		;
		container.addChild(view1, Point.new(0, 0));
		container.addChild(view2, Point.new(2, 2));

		container.removeChild(view1);
		container.removeChild(view2);

		this.assertContainerIsNotParentOfView(container, view1);
		this.assertContainerIsNotParentOfView(container, view2);
	}

	test_it_should_be_possible_to_add_child_views_to_a_container_2 {
		var
			container = GRContainerView.newDetached(4, 4),
			view1 = GRView.new(container, Point.new(0, 0), 2, 2),
			view2 = GRView.new(container, Point.new(2, 2), 2, 2)
		;

		this.assertContainerIsParentOfView(container, view1);
		this.assertContainerIsParentOfView(container, view2);
	}

	test_it_should_be_possible_to_remove_child_views_from_a_container_2 {
		var
			container = GRContainerView.newDetached(4, 4),
			view1 = GRView.new(container, Point.new(0, 0), 2, 2),
			view2 = GRView.new(container, Point.new(2, 2), 2, 2)
		;

		view1.remove;
		view2.remove;

		this.assertContainerIsNotParentOfView(container, view1);
		this.assertContainerIsNotParentOfView(container, view2);
	}

	test_it_should_be_possible_to_check_if_a_child_view_cover_a_specific_point {
		var container, view1, view2;
		container = GRContainerView.newDetached(4, 4);
		view1 = GRView.new(container, Point.new(1, 1), 2, 2);
		view2 = GRView.newDisabled(container, Point.new(2, 2), 2, 2);
		this.assert( container.hasChildAt(Point.new(2, 2)) );
		this.assertEqual( false, container.hasChildAt(Point.new(0, 3)) );
	}

	test_it_should_be_possible_to_retrieve_child_views_covering_a_specific_point {
		var container, view1, view2;
		container = GRContainerView.newDetached(4, 4);
		view1 = GRView.new(container, Point.new(1, 1), 2, 2);
		view2 = GRView.newDisabled(container, Point.new(2, 2), 2, 2);
		this.assertEqual( [ view1 ], container.getChildrenAt(Point.new(1, 1)) );
		this.assertEqual( [ view1, view2 ], container.getChildrenAt(Point.new(2, 2)) );
		this.assertEqual( [], container.getChildrenAt(Point.new(0, 1)) );
	}

	test_it_should_be_possible_to_check_if_an_enabled_child_view_cover_a_specific_point {
		var container, view1, view2;
		container = GRContainerView.newDetached(4, 4);
		view1 = GRView.new(container, Point.new(1, 1), 2, 2);
		view2 = GRView.newDisabled(container, Point.new(2, 2), 2, 2);
		this.assert( container.hasEnabledChildAt(Point.new(2, 2)) );
		this.assertEqual( false, container.hasEnabledChildAt(Point.new(3, 3)) );
		this.assertEqual( false, container.hasEnabledChildAt(Point.new(0, 3)) );
	}

	test_it_should_be_possible_to_retrieve_an_enabled_child_view_covering_a_specific_point {
		var container, view1, view2;
		container = GRContainerView.newDetached(4, 4);
		view1 = GRView.new(container, Point.new(1, 1), 2, 2);
		view2 = GRView.newDisabled(container, Point.new(2, 2), 2, 2);
		this.assertEqual( view1, container.getEnabledChildAt(Point.new(2, 2)) );
		this.assertEqual( nil, container.getEnabledChildAt(Point.new(0, 1)) );
	}

	test_it_should_not_be_possible_to_add_an_enabled_child_view_so_that_it_overlaps_with_other_enabled_child_views_in_the_container {
		var container;
		container = GRContainerView.newDetached(4, 4);
		GRView.new(container, Point.new(0, 0), 2, 2);
		this.assertErrorThrown(Error) { GRView.new(container, Point.new(1, 1), 3, 3) };
		this.assertNoErrorThrown { GRView.new(container, Point.new(2, 2), 2, 2) };
	}

	test_a_child_view_may_not_be_added_if_any_button_is_pressed_in_its_bounds_on_parent {
		var container, view;
		container = GRContainerView.newDetached(4, 4);
		view = GRView.newDetached(2, 2);
		container.press(Point.new(2, 2));
		this.assertErrorThrown(Error) { container.addChild(view, Point.new(1, 1)) };
		container.release(Point.new(2, 2));
		this.assertNoErrorThrown { container.addChild(view, Point.new(1, 1)) };
	}

	test_when_a_child_view_is_added_it_should_automatically_be_refreshed {
		var container, view;

		container = GRContainerView.newDetached(4, 4);
		container.id = \container;
		view = GRView.newDetached(2, 2);
		view.id = \view;

		this.addTestLedEventListener(container);

		container.addChild(view, Point.new(1, 1));

		this.assertEqual(
			[
				( source: \view, point: Point.new(1, 1), on: false ),
				( source: \view, point: Point.new(2, 1), on: false ),
				( source: \view, point: Point.new(1, 2), on: false ),
				( source: \view, point: Point.new(2, 2), on: false )
			],
			gotLed
		);
	}

	test_when_a_child_view_is_removed_its_bounds_on_parent_should_automatically_be_refreshed {
		var container, view;

		container = GRContainerView.newDetached(4, 4);
		container.id = \container;
		view = GRView.new(container, Point.new(1, 1), 2, 2);
		view.id = \view;

		this.addTestLedEventListener(container);

		container.removeChild(view);

		this.assertEqual(
			[
				( source: \container, point: Point.new(1, 1), on: false ),
				( source: \container, point: Point.new(2, 1), on: false ),
				( source: \container, point: Point.new(1, 2), on: false ),
				( source: \container, point: Point.new(2, 2), on: false )
			],
			gotLed
		);
	}

	test_it_should_be_possible_to_retrieve_a_child_views_all_parents {
		var container1, container2, container3, view;
		container1 = GRContainerView.newDetached(4, 4);
		container2 = GRContainerView.new(container1, Point.new(0,0));
		container3 = GRContainerView.new(container2, Point.new(0,0));
		view = GRView.new(container3, Point.new(0,0));
		this.assertEqual(
			[container3, container2, container1],
			view.getParents
		);
	}

	test_it_should_not_be_possible_to_add_a_child_view_to_a_container_twice {
		var container, view;
		container = GRContainerView.newDetached(4, 4);
		view = GRView.new(container, Point.new(0, 0), 2, 2);
		this.assertErrorThrown(Error) { container.addChild(view, Point.new(0, 0)) };
	}

	test_it_should_not_be_possible_to_add_a_child_that_already_has_a_parent {
		var container1, container2, view;
		container1 = GRContainerView.newDetached(4, 4);
		container2 = GRContainerView.newDetached(4, 4);
		view = GRView.new(container1, Point.new(0, 0), 2, 2);
		this.assertErrorThrown(Error) { container2.addChild(view, Point.new(0, 0)) };
	}

	test_it_should_not_be_possible_to_add_a_child_view_at_a_negative_origin {
		var container, view;
		container = GRContainerView.newDetached(4, 4);
		view = GRView.newDetached(2, 2);
		this.assertErrorThrown(Error) { container.addChild(view, Point.new(-1, 1)) };
		this.assertErrorThrown(Error) { container.addChild(view, Point.new(1, -1)) };
		this.assertErrorThrown(Error) { container.addChild(view, Point.new(-1, -1)) };
	}

	// Button Events
	test_button_events_on_a_container_should_be_forwarded_to_any_enabled_child_covering_the_pressed_or_released_point {
		var topContainer, childContainer, view;
		topContainer = GRContainerView.newDetached(4, 4);
		childContainer = GRContainerView.new(topContainer, Point.new(1, 1), 3, 3);
		view = GRContainerView.new(childContainer, Point.new(1, 1), 2, 2);

		this.assertEqual(
			[
				(view: view, point: Point.new(0, 0))
			],
			topContainer.press(Point.new(2, 2))
		);
		this.assert( view.isPressedAt(Point.new(0, 0)) );

		this.assertEqual(
			[
				(view: view, point: Point.new(0, 0))
			],
			topContainer.release(Point.new(2, 2))
		);
		this.assert( view.isReleasedAt(Point.new(0, 0)) );
	}

	test_on_non_press_through_containers_forwarded_button_events_should_not_affect_button_event_state {
		var topContainer, childContainer, view;
		topContainer = GRContainerView.newDetached(8, 8, true, false);
		childContainer = GRContainerView.new(topContainer, Point.new(1, 1), 3, 3, true, false);
		view = GRView.new(childContainer, Point.new(1, 1), 2, 2);

		this.assertEqual(
			[
				(view: view, point: Point.new(0, 0))
			],
			topContainer.press(Point.new(2, 2))
		);
		this.assert( view.isPressedAt(Point.new(0, 0)) );
		this.assert( childContainer.isReleasedAt(Point.new(1, 1)) );
		this.assert( topContainer.isReleasedAt(Point.new(2, 2)) );
	}

	test_on_press_through_containers_forwarded_button_events_should_should_be_saved {
		var topContainer, childContainer, view;
		topContainer = GRContainerView.newDetached(8, 8, true, true);
		childContainer = GRContainerView.new(topContainer, Point.new(1, 1), 3, 3, true, true);
		view = GRView.new(childContainer, Point.new(1, 1), 2, 2);

		this.assertEqual(
			[
				(view: view, point: Point.new(0, 0)),
				(view: childContainer, point: Point.new(1, 1)),
				(view: topContainer, point: Point.new(2, 2))
			],
			topContainer.press(Point.new(2, 2))
		);

		this.assert( view.isPressedAt(Point.new(0, 0)) );
		this.assert( childContainer.isPressedAt(Point.new(1, 1)) );
		this.assert( topContainer.isPressedAt(Point.new(2, 2)) );
	}

	test_when_all_buttons_are_released_on_non_press_through_containers_all_buttons_on_child_views_should_be_released {
		var topContainer, childContainer, view;
		topContainer = GRContainerView.newDetached(8, 8, true, false);
		childContainer = GRContainerView.new(topContainer, Point.new(1, 1), 3, 3, true, false);
		view = GRView.new(childContainer, Point.new(1, 1), 2, 2);

		topContainer.asPoints.do { |point| topContainer.press(point) };
		topContainer.releaseAll;

		this.assert( topContainer.allReleased );
		this.assert( childContainer.allReleased );
		this.assert( view.allReleased );
	}

	test_when_all_buttons_are_released_on_press_through_containers_all_buttons_on_child_views_should_be_released {
		var topContainer, childContainer, view;
		topContainer = GRContainerView.newDetached(8, 8, true, true);
		childContainer = GRContainerView.new(topContainer, Point.new(1, 1), 3, 3, true, true);
		view = GRView.new(childContainer, Point.new(1, 1), 2, 2);

		topContainer.asPoints.do { |point| topContainer.press(point) };
		topContainer.releaseAll;

		this.assert( topContainer.allReleased );
		this.assert( childContainer.allReleased );
		this.assert( view.allReleased );
	}

	// Led Events
	test_led_events_should_be_forwarded_to_parent {
		var topContainer, childContainer, view;

		topContainer = GRContainerView.newDetached(4, 4);
		topContainer.id = \topContainer;
		childContainer = GRContainerView.new(topContainer, Point.new(1, 1), 3, 3);
		childContainer.id = \childContainer;
		view = GRView.new(childContainer, Point.new(1, 1), 2, 2);
		view.id = \view;

		this.addTestLedEventListener(topContainer);

		topContainer.isLitFunc = { false };
		childContainer.isLitFunc = { true };
		view.isLitFunc = { false };

		topContainer.refreshPoint(Point.new(0, 0));
		this.assertEqual(
			[
				( source: \topContainer, point: Point.new(0, 0), on: false )
			],
			gotLed
		);

		gotLed = Array.new; // TODO: or reset_test_led_event_listener_x

		childContainer.refreshPoint(Point.new(0, 0));
		this.assertEqual(
			[
				( source: \childContainer, point: Point.new(1, 1), on: true )
			],
			gotLed
		);

		gotLed = Array.new; // TODO: or reset_test_led_event_listener_x

		view.refreshPoint(Point.new(0, 0));
		this.assertEqual(
			[
				( source: \view, point: Point.new(2, 2), on: false )
			],
			gotLed
		);
	}

	test_refresh {
		var topContainer, childContainer, view;

		topContainer = GRContainerView.newDetached(4, 4);
		topContainer.id = \topContainer;
		childContainer = GRContainerView.new(topContainer, Point.new(1, 1), 3, 3);
		childContainer.id = \childContainer;
		view = GRView.new(childContainer, Point.new(1, 1), 2, 2);
		view.id = \view;

		this.addTestLedEventListener(topContainer);

		topContainer.isLitFunc = { |point| true };
		childContainer.isLitFunc = { |point| false };
		view.isLitFunc = { |point| true };

		topContainer.refreshPoint(Point.new(0, 0));
		this.assertEqual(
			[
				( source: \topContainer, point: Point.new(0, 0), on: true )
			],
			gotLed
		);

		gotLed = Array.new; // TODO: or reset_test_led_event_listener_x

		topContainer.refreshPoint(Point.new(1, 1));
		this.assertEqual(
			[
				( source: \childContainer, point: Point.new(1, 1), on: false )
			],
			gotLed
		);

		gotLed = Array.new; // TODO: or reset_test_led_event_listener_x

		topContainer.refreshPoint(Point.new(2, 2));
		this.assertEqual(
			[
				( source: \view, point: Point.new(2, 2), on: true )
			],
			gotLed
		);

		gotLed = Array.new; // TODO: or reset_test_led_event_listener_x

		childContainer.refreshPoint(Point.new(0, 0));
		this.assertEqual(
			[
				( source: \childContainer, point: Point.new(1, 1), on: false )
			],
			gotLed
		);

		gotLed = Array.new; // TODO: or reset_test_led_event_listener_x

		childContainer.refreshPoint(Point.new(1, 1));
		this.assertEqual(
			[
				( source: \view, point: Point.new(2, 2), on: true )
			],
			gotLed
		);

		gotLed = Array.new; // TODO: or reset_test_led_event_listener_x

		view.refreshPoint(Point.new(1, 1));
		this.assertEqual(
			[
				( source: \view, point: Point.new(3, 3), on: true )
			],
			gotLed
		);

		gotLed = Array.new; // TODO: or reset_test_led_event_listener_x

		topContainer.refresh;
		this.assertEqual(
			[
				( source: \topContainer, point: Point.new(0, 0), on: true ),
				( source: \topContainer, point: Point.new(1, 0), on: true ),
				( source: \topContainer, point: Point.new(2, 0), on: true ),
				( source: \topContainer, point: Point.new(3, 0), on: true ),
				( source: \topContainer, point: Point.new(0, 1), on: true ),
				( source: \childContainer, point: Point.new(1, 1), on: false ),
				( source: \childContainer, point: Point.new(2, 1), on: false ),
				( source: \childContainer, point: Point.new(3, 1), on: false ),
				( source: \topContainer, point: Point.new(0, 2), on: true ),
				( source: \childContainer, point: Point.new(1, 2), on: false ),
				( source: \view, point: Point.new(2, 2), on: true ),
				( source: \view, point: Point.new(3, 2), on: true ),
				( source: \topContainer, point: Point.new(0, 3), on: true ),
				( source: \childContainer, point: Point.new(1, 3), on: false ),
				( source: \view, point: Point.new(2, 3), on: true ),
				( source: \view, point: Point.new(3, 3), on: true )
			],
			gotLed
		);

		gotLed = Array.new; // TODO: or reset_test_led_event_listener_x

		topContainer.refresh(false);
		this.assertEqual(
			[
				( source: \topContainer, point: Point.new(0, 0), on: true ),
				( source: \topContainer, point: Point.new(1, 0), on: true ),
				( source: \topContainer, point: Point.new(2, 0), on: true ),
				( source: \topContainer, point: Point.new(3, 0), on: true ),
				( source: \topContainer, point: Point.new(0, 1), on: true ),
				( source: \topContainer, point: Point.new(0, 2), on: true ),
				( source: \topContainer, point: Point.new(0, 3), on: true )
			],
			gotLed
		);

	}

	// String Representation
	test_string_representation {
		var container;
		container = GRContainerView.newDetached(4, 3);
		this.assertEqual(
			"a GRContainerView (4x3, enabled)",
			container.asString
		);
		container.id = \test;
		this.assertEqual(
			"a GRContainerView (test, 4x3, enabled)",
			container.asString
		);
	}

	test_plot {
		var container, view;
		container = GRContainerView.newDetached(4, 3);
		view = GRView.new(container, Point.new(1, 1), 2, 2);
		this.assertEqual(
			"   0   1   2   3        0   1   2   3 \n" ++
			"0  -   -   -   -     0  -   -   -   - \n" ++
			"1  -  [-] [-]  -     1  -  [-] [-]  - \n" ++
			"2  -  [-] [-]  -     2  -  [-] [-]  - \n",
			container.asPlot
		);
		container.press(Point.new(0, 0));
		view.press(Point.new(0, 1));
		container.isLitFunc = { |point| point.x.odd };
		this.assertEqual(
			"   0   1   2   3        0   1   2   3 \n" ++
			"0  P   -   -   -     0  -   L   -   L \n" ++
			"1  -  [-] [-]  -     1  -  [-] [-]  L \n" ++
			"2  -  [-] [-]  -     2  -  [-] [-]  L \n",
			container.asPlot
		);
		view.disable;
		this.assertEqual(
			"   0   1   2   3        0   1   2   3 \n" ++
			"0  P   -   -   -     0  -   L   -   L \n" ++
			"1  -   -   -   -     1  -   L   -   L \n" ++
			"2  -   -   -   -     2  -   L   -   L \n",
			container.asPlot
		);
	}

	test_plot_tree {
		var
			container = GRContainerView.newDetached(4, 3),
			view = GRView.new(container, Point.new(1, 1), 2, 2)
		;
		this.assertEqual(
			"a GRContainerView (4x3, enabled)\n" ++
			"   0   1   2   3        0   1   2   3 \n" ++
			"0  -   -   -   -     0  -   -   -   - \n" ++
			"1  -  [-] [-]  -     1  -  [-] [-]  - \n" ++
			"2  -  [-] [-]  -     2  -  [-] [-]  - \n" ++
			"\n"++
			"\n"++
			"\ta GRView (2x2, enabled)\n" ++
			"\t  0 1      0 1\n" ++
			"\t0 - -    0 - -\n" ++
			"\t1 - -    1 - -\n" ++
			"\n",
			container.asTree(true)
		)
	}
}

GridSwitcherTests : Test {
	var
		switcher
	;

	setup {
		GRTestsHelper.saveGlobals;
		GRTestsHelper.disableTraceAndFlash;
		switcher = GridSwitcher.newDetached(4, 4);
	}

	teardown {
		GRTestsHelper.restoreGlobals;
	}

	// Initialization
	test_defaults {
		this.assertEqual(nil, switcher.value);
	}

	// Switching Views
	test_switching_views {
		var child1, child2, child3;
		child1 = GRView.new(switcher, Point.new(0, 0));
		child2 = GRView.newDisabled(switcher, Point.new(0, 0));
		child3 = GRView.newDisabled(switcher, Point.new(0, 0));
		this.assertEqual(child1, switcher.currentView);

		switcher.value = 1;
		this.assert(child1.isDisabled);
		this.assert(child2.isEnabled);
		this.assertEqual(child2, switcher.currentView);

		switcher.value = 2;
		this.assert(child2.isDisabled);
		this.assert(child3.isEnabled);
		this.assertEqual(child3, switcher.currentView);
		this.assertErrorThrown(Error) { switcher.value = 3 };
	}

	test_switching_views_while_button_is_pressed {
		GRView.new(switcher, Point.new(0, 0));
		GRView.newDisabled(switcher, Point.new(0, 0));
		switcher.press(Point.new(2, 2));
		switcher.value = 1;
		this.assertNoErrorThrown { switcher.release(Point.new(2, 2)) };
	}

}
*/
