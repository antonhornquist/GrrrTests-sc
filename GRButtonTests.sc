GRButtonTests : Test {
	setup {
		GRTestsHelper.saveGlobals;
		GRTestsHelper.disableTraceAndFlash;
	}

	teardown {
		GRTestsHelper.restoreGlobals;
	}

	// initialization
	test_a_button_should_by_default_be_a_released_coupled_toggle_button_with_value_false {
		var button = GRButton.newDetached;
		this.assert(button.isReleased);
		this.assert(button.isCoupled);
		this.assertEqual(\toggle, button.behavior);
		this.assertEqual(false, button.value);
	}

	test_it_should_be_possible_to_set_a_buttons_behavior_to_momentary {
		var button = GRButton.newDetached;
		button.behavior = \momentary;
		this.assertEqual(\momentary, button.behavior);
	}

	test_it_should_be_possible_to_create_a_momentary_button_from_scratch {
		var button = GRButton.newMomentary(nil, nil, 1, 1);
		this.assertEqual(\momentary, button.behavior);
	}

	test_it_should_be_possible_to_decouple_a_button {
		var button = GRButton.newDetached;
		button.coupled = false;
		this.assertEqual(false, button.isCoupled);
	}

	test_it_should_be_possible_to_create_a_decoupled_button_from_scratch {
		var button = GRButton.newDecoupled(nil, nil, 1, 1);
		this.assertEqual(false, button.isCoupled);
	}

	// button pressed state and button events
	test_a_single_view_button_press_event_should_make_a_button_pressed {
		var button = GRButton.newDetached(1, 1);
		button.press(Point.new(0, 0));
		this.assert(button.isPressed);
	}

	test_a_button_should_not_be_considered_released_until_all_view_buttons_are_released {
		var button = GRButton.newDetached(2, 2);

		button.press(Point.new(0, 0));

		this.assert(button.isPressed);

		button.press(Point.new(1, 0));

		this.assert(button.isPressed);

		button.release(Point.new(0, 0));

		this.assert(button.isPressed);

		button.release(Point.new(1, 0));

		this.assert(button.isReleased);
	}

	test_when_pressed_state_of_a_button_is_updated_button_pressed_and_released_actions_should_be_triggered {
		var button = GRButton.newDecoupled(nil, nil, 2, 2);
		var pressedListener = MockButtonPressedListener.new(button);
		var releasedListener = MockButtonReleasedListener.new(button);

		button.press(Point.new(0, 0));
		this.assert( pressedListener.hasBeenNotifiedOf( [ [button] ] ) );

		button.press(Point.new(1, 0));
		this.assert( pressedListener.hasBeenNotifiedOf( [ [button] ] ) );

		button.release(Point.new(0, 0));
		this.assert( releasedListener.hasNotBeenNotifiedOfAnything );

		button.release(Point.new(1, 0));
		this.assert( releasedListener.hasBeenNotifiedOf( [ [button] ] ) );
	}

	// led events and refresh
	test_when_the_value_of_a_button_is_set_to_true_leds_are_lit {
		var button = GRButton.newDetached(2, 2);
		var listener = MockViewLedRefreshedListener.new(button);
		button.id = \abc;

		button.value = true;

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					( source: \abc, point: Point.new(0, 0), on: true ),
					( source: \abc, point: Point.new(1, 0), on: true ),
					( source: \abc, point: Point.new(0, 1), on: true ),
					( source: \abc, point: Point.new(1, 1), on: true ),
				]
			)
		);
	}

	test_when_the_value_of_a_button_is_set_to_false_leds_are_unlit {
		var button = GRButton.newDetached(2, 2);
		var listener;
		button.id = \abc;
		button.value = true;
		listener = MockViewLedRefreshedListener.new(button);

		button.value = false;

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

	// decoupled button behavior
	test_a_decoupled_button_should_not_toggle_value_nor_trigger_main_action_when_it_is_pressed_and_when_it_is_released {
		var button = GRButton.newDecoupled(nil, nil, 2, 2);
		var listener = MockActionListener.new(button);

		this.assertEqual(false, button.value);
		button.press(Point.new(0, 0));
		this.assertEqual(false, button.value);
		button.release(Point.new(0, 0));
		this.assertEqual(false, button.value);

		this.assert( listener.hasNotBeenNotifiedOfAnything );
	}

	// coupled toggle button behavior
	test_a_coupled_toggle_button_should_toggle_value_every_time_the_button_is_pressed {
		var button = GRButton.newDetached(1, 1);

		button.press(Point.new(0, 0));
		this.assertEqual(true, button.value);
		button.release(Point.new(0, 0));
		this.assertEqual(true, button.value);
		button.press(Point.new(0, 0));
		this.assertEqual(false, button.value);
		button.release(Point.new(0, 0));
		this.assertEqual(false, button.value);
	}

	test_a_coupled_toggle_button_should_trigger_the_main_action_every_time_a_button_is_pressed {
		var button = GRButton.newDetached(1, 1);
		var listener = MockActionListener.new(button);

		button.press(Point.new(0, 0));

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					[button, true]
				]
			)
		);

		button.release(Point.new(0, 0));

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					[button, true]
				]
			)
		);

		button.press(Point.new(0, 0));

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					[button, true],
					[button, false]
				]
			)
		);

		button.release(Point.new(0, 0));

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					[button, true],
					[button, false]
				]
			)
		);
	}

	// coupled momentary button behavior
	test_a_coupled_momentary_button_should_toggle_value_both_when_button_is_pressed_and_when_it_is_released {
		var button = GRButton.newMomentary(nil, nil, 1, 1);

		button.press(Point.new(0, 0));
		this.assertEqual(true, button.value);
		button.release(Point.new(0, 0));
		this.assertEqual(false, button.value);

		button.value=true;

		button.press(Point.new(0, 0));
		this.assertEqual(false, button.value);
		button.release(Point.new(0, 0));
		this.assertEqual(true, button.value);
	}

	test_a_coupled_momentary_button_should_trigger_main_action_both_when_button_is_pressed_and_when_it_is_released {
		var button = GRButton.newMomentary(nil, nil, 1, 1);
		var listener = MockActionListener.new(button);

		button.press(Point.new(0, 0));

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					[button, true]
				]
			)
		);

		button.release(Point.new(0, 0));

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					[button, true],
					[button, false]
				]
			)
		);

		button.value=true;

		button.press(Point.new(0, 0));

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					[button, true],
					[button, false],
					[button, false]
				]
			)
		);

		button.release(Point.new(0, 0));

		this.assert(
			listener.hasBeenNotifiedOf(
				[
					[button, true],
					[button, false],
					[button, false],
					[button, true]
				]
			)
		);
	}
}
