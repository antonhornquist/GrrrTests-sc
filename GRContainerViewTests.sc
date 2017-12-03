GRContainerViewTests : Test {
	setup {
		GRTestsHelper.saveGlobals;
		GRTestsHelper.disableTraceAndFlash;
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
		var container = GRContainerView.newDetached(2, 3);
		var view = GRView.newDetached(2, 2);
		this.assert(container.isWithinBounds(view, Point.new(0, 0)));
		this.assertEqual(false, container.isWithinBounds(view, Point.new(2, 2)));
		this.assertNoErrorThrown { container.validateWithinBounds(view, Point.new(0, 0)) };
		this.assertErrorThrown(Error) { container.validateWithinBounds(view, Point.new(2, 2)) };
	}

	test_it_should_be_possible_to_determine_whether_a_container_is_parent_of_a_view {
		var container = GRContainerView.newDetached(2, 2);
		var view1 = GRView.new(container, Point.new(0, 0), 2, 2);
		var view2 = GRView.newDetached(2, 2);
		this.assertNoErrorThrown { container.validateParentOf(view1) };
		this.assertErrorThrown(Error) { container.validateParentOf(view2) };
	}

	test_if_a_container_has_no_children_it_should_be_considered_empty {
		var container = GRContainerView.newDetached(2, 2);

		this.assert(container.isEmpty);
	}

	test_if_a_container_has_one_or_more_children_it_should_not_be_considered_empty {
		var container = GRContainerView.newDetached(2, 2);
		GRView.new(container, Point.new(0, 0), 2, 2);

		this.assertEqual(false, container.isEmpty);
	}

	// parent - child
	test_it_should_be_possible_to_attach_a_child_view_to_a_container_view_on_creation_of_the_child_view {
		var container = GRContainerView.newDetached(4, 4);
		var view1 = GRView.new(container, Point.new(0, 0), 2, 2);
		var view2 = GRView.new(container, Point.new(2, 2), 2, 2);

		this.assertContainerIsParentOfView(container, view1);
		this.assertEqual(Point.new(0, 0), view1.origin);
		this.assertContainerIsParentOfView(container, view2);
		this.assertEqual(Point.new(2, 2), view2.origin);
	}

	test_it_should_be_possible_to_attach_a_detached_view_as_a_child_to_a_container_view {
		var container = GRContainerView.newDetached(4, 4);
		var view1 = GRView.newDetached(2, 2);
		var view2 = GRView.newDetached(2, 2);

		container.addChild(view1, Point.new(0, 0));
		container.addChild(view2, Point.new(2, 2));

		this.assertContainerIsParentOfView(container, view1);
		this.assertEqual(Point.new(0, 0), view1.origin);
		this.assertContainerIsParentOfView(container, view2);
		this.assertEqual(Point.new(2, 2), view2.origin);
	}

	test_it_should_not_be_possible_to_attach_a_parent_of_a_container_view_as_its_child_view {
		var parentContainer = GRContainerView.newDetached(4, 4);
		var container = GRContainerView.new(parentContainer, Point.new(0, 0), 4, 4);
		this.assertErrorThrown(Error) { container.addChild(parentContainer, Point.new(0, 0)) };
	}

	test_it_should_be_possible_to_remove_child_views_from_a_container_view {
		var container = GRContainerView.newDetached(4, 4);
		var view1 = GRView.newDetached(2, 2);
		var view2 = GRView.newDetached(2, 2);
		container.addChild(view1, Point.new(0, 0));
		container.addChild(view2, Point.new(2, 2));

		container.removeChild(view1);
		view2.remove;

		this.assertContainerIsNotParentOfView(container, view1);
		this.assertContainerIsNotParentOfView(container, view2);
	}

	test_it_should_be_possible_to_remove_all_child_views_of_a_container_view {
		var container = GRContainerView.newDetached(4, 4);
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
		var container = GRContainerView.newDetached(4, 4);
		var view = GRView.newDetached(2, 2);
		this.assertErrorThrown(Error) { container.addChild(view, nil) };
	}

	test_trying_to_remove_a_detached_view_should_throw_an_error {
		this.assertErrorThrown(Error) { GRView.newDetached(4, 4).remove };
	}

	test_it_should_be_possible_to_determine_whether_any_enabled_or_disabled_child_views_cover_a_specific_point {
		var container = GRContainerView.newDetached(4, 4);
		GRView.new(container, Point.new(1, 1), 2, 2);
		GRView.newDisabled(container, Point.new(2, 2), 2, 2);

		this.assert(container.hasChildAt(Point.new(2, 2)));
		this.assertEqual( false, container.hasChildAt(Point.new(0, 3)) );
	}

	test_it_should_be_possible_to_retrieve_all_enabled_and_disabled_child_views_covering_a_specific_point {
		var container = GRContainerView.newDetached(4, 4);
		var view1 = GRView.new(container, Point.new(1, 1), 2, 2);
		var view2 = GRView.newDisabled(container, Point.new(2, 2), 2, 2);

		this.assertEqual( [ view1 ], container.getChildrenAt(Point.new(1, 1)) );
		this.assertEqual( [ view1, view2 ], container.getChildrenAt(Point.new(2, 2)) );
		this.assertEqual( [], container.getChildrenAt(Point.new(0, 1)) );
	}

	test_it_should_be_possible_to_determine_if_any_enabled_child_views_cover_a_specific_point {
		var container = GRContainerView.newDetached(4, 4);
		GRView.new(container, Point.new(1, 1), 2, 2);
		GRView.newDisabled(container, Point.new(2, 2), 2, 2);

		this.assert(container.hasAnyEnabledChildAt(Point.new(2, 2)));
		this.assertEqual( false, container.hasAnyEnabledChildAt(Point.new(3, 3)) );
		this.assertEqual( false, container.hasAnyEnabledChildAt(Point.new(0, 3)) );
	}

	test_it_should_be_possible_to_retrieve_the_topmost_enabled_child_view_covering_a_specific_point {
		var container = GRContainerView.newDetached(4, 4);
		var view1 = GRView.new(container, Point.new(1, 1), 2, 2);
		GRView.newDisabled(container, Point.new(2, 2), 2, 2);

		this.assertEqual( view1, container.getTopmostEnabledChildAt(Point.new(2, 2)) );
		this.assertEqual( nil, container.getTopmostEnabledChildAt(Point.new(0, 1)) );
	}

	test_when_an_enabled_child_view_is_added_to_a_container_that_has_buttons_pressed_on_child_views_bounds_the_buttons_should_be_released_on_the_container_before_the_child_view_is_added {
		var container = GRContainerView.newDetached(4, 4);
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
		var container1 = GRContainerView.newDetached(4, 4);
		var container2 = GRContainerView.new(container1, Point.new(0,0), 4, 4);
		var container3 = GRContainerView.new(container2, Point.new(0,0), 4, 4);
		var view = GRView.new(container3, Point.new(0,0), 4, 4);

		this.assertEqual(
			[container3, container2, container1],
			view.getParents
		);
	}

	test_it_should_not_be_possible_to_add_a_view_as_a_child_to_a_container_if_the_view_already_has_a_parent {
		var container1 = GRContainerView.newDetached(4, 4);
		var container2 = GRContainerView.newDetached(4, 4);
		var view = GRView.new(container1, Point.new(0, 0), 2, 2);
		this.assertErrorThrown(Error) { container2.addChild(view, Point.new(0, 0)) };
	}

	test_it_should_not_be_possible_to_add_a_child_view_at_a_negative_origin {
		var container = GRContainerView.newDetached(4, 4);
		var view = GRView.newDetached(2, 2);
		this.assertErrorThrown(Error) { container.addChild(view, Point.new(-1, 1)) };
		this.assertErrorThrown(Error) { container.addChild(view, Point.new(1, -1)) };
		this.assertErrorThrown(Error) { container.addChild(view, Point.new(-1, -1)) };
	}

	test_when_a_child_view_is_enabled_on_a_container_that_have_buttons_pressed_on_the_child_views_bounds_the_buttons_should_be_released_on_the_container_before_the_child_view_is_enabled {
		var container = GRContainerView.newDetached(4, 4);
		var view = GRView.newDisabled(container, Point.new(1, 1), 2, 2);
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
		var topContainer = GRContainerView.newDetached(4, 4);
		var childContainer = GRContainerView.new(topContainer, Point.new(1, 1), 3, 3);
		var view = GRContainerView.new(childContainer, Point.new(1, 1), 2, 2);

		this.assertEqual(
			[
				( view: view, point: Point.new(0, 0) )
			],
			topContainer.press(Point.new(2, 2))
		);
		this.assert(view.isPressedAt(Point.new(0, 0)));
	}

	test_a_containers_incoming_button_events_should_not_be_forwarded_to_any_disabled_child_views_that_cover_the_affected_button {
		var container = GRContainerView.newDetached(4, 4);
		var view = GRView.new(container, Point.new(0,0), 4, 4);

		view.disable;

		this.assertEqual(
			[
				( view: container, point: Point.new(0, 0) )
			],
			container.press(Point.new(0, 0))
		);
	}

	test_when_incoming_button_events_are_forwarded_by_non_press_through_containers_they_should_not_be_handled_on_the_container {
		var topContainer = GRContainerView.newDetached(4, 4, true, false);
		var childContainer = GRContainerView.new(topContainer, Point.new(1, 1), 3, 3, true, false);
		var view = GRContainerView.new(childContainer, Point.new(1, 1), 2, 2);

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
		var topContainer = GRContainerView.newDetached(8, 8, true, true);
		var childContainer = GRContainerView.new(topContainer, Point.new(1, 1), 3, 3, true, true);
		var view = GRView.new(childContainer, Point.new(1, 1), 2, 2);

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
		var topContainer = GRContainerView.newDetached(8, 8);
		var childContainer = GRContainerView.new(topContainer, Point.new(0, 0), 8, 8);
		var view1 = GRView.new(childContainer, Point.new(0, 0), 2, 2);
		var view2 = GRView.new(childContainer, Point.new(2, 2), 2, 2);
		topContainer.asPoints.do { |point| topContainer.press(point) };

		topContainer.disable;

		this.assert(topContainer.allReleased);
		this.assert(childContainer.allReleased);
		this.assert(view1.allReleased);
		this.assert(view2.allReleased);
	}

	test_when_a_press_through_container_view_is_disabled_all_its_pressed_buttons_and_all_its_enabled_childrens_pressed_buttons_should_be_released {
		var topContainer = GRContainerView.newDetached(8, 8, true, true);
		var childContainer = GRContainerView.new(topContainer, Point.new(0, 0), 8, 8, true, true);
		var view1 = GRView.new(childContainer, Point.new(0, 0), 2, 2);
		var view2 = GRView.new(childContainer, Point.new(2, 2), 2, 2);
		topContainer.asPoints.do { |point| topContainer.press(point) };

		topContainer.disable;

		this.assert(topContainer.allReleased);
		this.assert(childContainer.allReleased);
		this.assert(view1.allReleased);
		this.assert(view2.allReleased);
	}

	// led events and refresh
	test_if_a_point_of_a_container_is_refreshed_and_an_enabled_child_view_cover_the_point_the_child_view_led_state_should_override_container_led_state {
		var topContainer, childContainer, view, listener;
		topContainer = MockLitContainerView.newDetached(4, 4);
		topContainer.id = \topContainer;
		childContainer = GRContainerView.new(topContainer, Point.new(1, 1), 3, 3);
		childContainer.id = \childContainer;
		view = MockLitView.new(childContainer, Point.new(1, 1), 2, 2);
		view.id = \view;
		listener = MockViewLedRefreshedListener.new(topContainer);

		topContainer.refreshPoint(Point.new(0, 0));

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \topContainer, point: Point.new(0, 0), on: true )
				]
			)
		);

		topContainer.refreshPoint(Point.new(1, 1));

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \topContainer, point: Point.new(0, 0), on: true ),
					( source: \childContainer, point: Point.new(1, 1), on: false )
				]
			)
		);

		topContainer.refreshPoint(Point.new(2, 2));

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \topContainer, point: Point.new(0, 0), on: true ),
					( source: \childContainer, point: Point.new(1, 1), on: false ),
					( source: \view, point: Point.new(2, 2), on: true )
				]
			)
		);
	}

	test_when_an_area_of_a_container_is_refreshed_on_the_points_where_enabled_child_views_are_the_child_view_led_state_should_override_container_led_state {
		var topContainer, childContainer, view, listener;
		topContainer = MockLitContainerView.newDetached(4, 4);
		topContainer.id = \topContainer;
		childContainer = GRContainerView.new(topContainer, Point.new(1, 1), 3, 3);
		childContainer.id = \childContainer;
		view = MockLitView.new(childContainer, Point.new(1, 1), 2, 2);
		view.id = \view;
		listener = MockViewLedRefreshedListener.new(topContainer);

		topContainer.refreshBounds(Point.new(1, 1), 3, 2);

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
		var topContainer, childContainer, view, listener;
		topContainer = MockLitContainerView.newDetached(4, 4);
		topContainer.id = \topContainer;
		childContainer = GRContainerView.new(topContainer, Point.new(1, 1), 3, 3);
		childContainer.id = \childContainer;
		view = MockLitView.new(childContainer, Point.new(1, 1), 2, 2);
		view.id = \view;
		listener = MockViewLedRefreshedListener.new(topContainer);

		topContainer.refresh;

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
		var topContainer, childContainer, view, listener;
		topContainer = MockLitContainerView.newDetached(4, 4);
		topContainer.id = \topContainer;
		childContainer = GRContainerView.new(topContainer, Point.new(1, 1), 3, 3);
		childContainer.id = \childContainer;
		view = MockLitView.new(childContainer, Point.new(1, 1), 2, 2);
		view.id = \view;
		listener = MockViewLedRefreshedListener.new(topContainer);

		view.refresh;

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
		var container = GRContainerView.newDetached(4, 4);
		var view = GRView.new(container, Point.new(0,0), 4, 4);
		var listener;
		container.disable;

		listener = MockViewLedRefreshedListener.new(container);

		view.refreshPoint(Point.new(0, 0));
		view.refreshBounds(Point.new(1, 1), 1, 1);
		view.refresh;

		this.assert(listener.hasNotBeenNotifiedOfAnything);
	}

	test_it_should_be_possible_to_refresh_only_the_points_of_a_container_where_led_state_is_not_overridden_by_any_child_view {
		var topContainer, childContainer, view, listener;
		topContainer = MockLitContainerView.newDetached(4, 4);
		topContainer.id = \topContainer;
		childContainer = GRContainerView.new(topContainer, Point.new(1, 1), 3, 3);
		childContainer.id = \childContainer;
		view = MockLitView.new(childContainer, Point.new(1, 1), 2, 2);
		view.id = \view;
		listener = MockViewLedRefreshedListener.new(topContainer);

		topContainer.refresh(false);

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
		var container = GRContainerView.newDetached(4, 4);
		var view = GRView.newDetached(2, 2);
		var listener;
		container.id = \container;
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
		var container = GRContainerView.newDetached(4, 4);
		var view = GRView.new(container, Point.new(1, 1), 2, 2);
		var listener;
		container.id = \container;
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
		var container = GRContainerView.newDetached(4, 4);
		var view = GRView.new(container, Point.new(1, 1), 2, 2);
		var listener;
		container.id = \container;
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
		var container = GRContainerView.newDetached(4, 4);
		var view = GRView.newDisabled(container, Point.new(1, 1), 2, 2);
		var listener;
		container.id = \container;
		view.id = \view;

		listener = MockViewLedRefreshedListener.new(container);

		container.removeChild(view);

		this.assert(listener.hasNotBeenNotifiedOfAnything);
	}

	// string representations
	test_the_string_representation_of_a_container_view_should_include_id_bounds_and_whether_the_view_is_enabled {
		var container = GRContainerView.newDetached(4, 3);
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

	test_plot_should_indicate_enabled_children_of_a_view_and_where_the_view_currently_is_pressed_and_lit {
		var container = MockOddColsLitContainerView.newDetached(4, 3);
		var view = GRView.new(container, Point.new(1, 1), 2, 2);
		this.assertEqual(
			"   0   1   2   3        0   1   2   3 \n" ++
			"0  -   -   -   -     0  -   L   -   L \n" ++
			"1  -  [-] [-]  -     1  -  [-] [-]  L \n" ++
			"2  -  [-] [-]  -     2  -  [-] [-]  L \n",
			container.asPlot
		);
		container.press(Point.new(0, 0));
		view.press(Point.new(0, 1));
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

	test_a_tree_plot_of_a_view_should_indicate_where_buttons_and_leds_are_currently_pressed_and_lit_and_also_include_its_string_representation_and_also_recursively_print_its_childrens_tree_plots {
		var topContainer = GRContainerView.newDetached(4, 3);
		var container2 = GRContainerView.new(topContainer, Point.new(2, 1), 2, 2);
		GRView.new(container2, Point.new(0, 0), 2, 1);
		GRView.new(topContainer, Point.new(0, 0), 2, 1);
		this.assertEqual(
			"a GRContainerView (4x3, enabled)\n" ++
			"   0   1   2   3        0   1   2   3 \n" ++
			"0 [-] [-]  -   -     0 [-] [-]  -   - \n" ++
			"1  -   -  [-] [-]    1  -   -  [-] [-]\n" ++
			"2  -   -  [-] [-]    2  -   -  [-] [-]\n" ++
			"\n" ++
			"\ta GRContainerView (2x2, enabled)\n" ++
			"\t   0   1        0   1 \n" ++
			"\t0 [-] [-]    0 [-] [-]\n" ++
			"\t1  -   -     1  -   - \n" ++
			"\n" ++
			"\t\ta GRView (2x1, enabled)\n" ++
			"\t\t  0 1      0 1\n" ++
			"\t\t0 - -    0 - -\n" ++
			"\n" ++
			"\ta GRView (2x1, enabled)\n" ++
			"\t  0 1      0 1\n" ++
			"\t0 - -    0 - -\n" ++
			"\n",
			topContainer.asTree(true)
		);
	}
}
