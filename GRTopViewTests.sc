GRTopViewTests : Test {

	setup {
		GRTestsHelper.saveGlobals;
		GRTestsHelper.disableTraceAndFlash;
	}

	teardown {
		GRTestsHelper.restoreGlobals;
	}

	// parent - child
	test_it_should_not_be_possible_to_add_a_top_view_as_a_child_to_another_view {
		var containerView = GRContainerView.newDetached(8, 8);
		var topView = GRTopView.new(8, 8);

		this.assertErrorThrown(Error) {
			containerView.addChild(topView, Point.new(0, 0))
		};
	}

	// button events and state
	test_a_top_view_button_should_be_considered_pressed_when_one_or_many_sources_have_emitted_button_press_events_to_it {
		var topView = GRTopView.new(8, 8);
		var controller1 = MockController.new(8, 8, topView, Point.new(0, 0));
		var controller2 = MockController.new(4, 4, topView, Point.new(2, 2));
		var listener = MockViewButtonStateChangedListener.new(topView);

		controller1.emitPress(Point.new(2, 2));

		this.assert(topView.isPressedAt(Point.new(2, 2)));
		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( point: Point.new(2, 2), pressed: true )
				]
			)
		);

		controller2.emitPress(Point.new(0, 0));

		this.assert(topView.isPressedAt(Point.new(2, 2)));
		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( point: Point.new(2, 2), pressed: true )
				]
			)
		);
	}

	test_a_top_view_button_should_not_be_considered_released_until_all_sources_that_pressed_the_button_have_emitted_button_release_events_to_it {
		var topView = GRTopView.new(8, 8);
		var controller1 = MockController.new(8, 8, topView, Point.new(0, 0));
		var controller2 = MockController.new(4, 4, topView, Point.new(2, 2));
		var listener;
		controller1.emitPress(Point.new(2, 2));
		controller2.emitPress(Point.new(0, 0));
		listener = MockViewButtonStateChangedListener.new(topView);

		controller1.emitRelease(Point.new(2, 2));

		this.assert(topView.isPressedAt(Point.new(2, 2)));
		this.assert(listener.hasNotBeenNotifiedOfAnything);

		controller2.emitRelease(Point.new(0, 0));

		this.assert(topView.isReleasedAt(Point.new(2, 2)));
		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( point: Point.new(2, 2), pressed: false )
				]
			)
		);
	}
}
