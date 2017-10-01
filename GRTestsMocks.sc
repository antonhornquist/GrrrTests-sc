MockActionListener {
	var <view, <listener, <notifications;

	*new { |view|
		^super.new.initMockActionListener(view);
	}

	initMockActionListener { |argView|
		view = argView;
		listener = this.prCreateListener(view);
		view.addAction(listener, this.prSelector);
		notifications = Array.new;
	}

	removeListener {
		view.removeAction(listener, this.prSelector);
	}

	hasBeenNotifiedOf { |array| ^notifications == array }

	hasNotBeenNotifiedOfAnything { ^notifications.isEmpty }

	prCreateListener { |view|
		^{ |... args| notifications = notifications.add( args ) }
	}

	prSelector { ^\action }
}

MockViewLedRefreshedListener : MockActionListener {
	prCreateListener { |view|
		^{ |source, point, on|
			notifications = notifications.add(
 				( source: source.id, point: point, on: on )
			)
		}
	}
	prSelector { ^\viewLedRefreshedAction }
}

MockViewButtonStateChangedListener : MockActionListener {
	prCreateListener { |view|
		^{ |point, pressed|
			notifications = notifications.add(
				( point: point, pressed: pressed )
			)
		}
	}
	prSelector { ^\viewButtonStateChangedAction }
}

MockViewWasEnabledListener : MockActionListener {
	prSelector { ^\viewWasEnabledAction }
}

MockViewWasDisabledListener : MockActionListener {
	prSelector { ^\viewWasDisabledAction }
}

MockButtonPressedListener : MockActionListener {
	prSelector { ^\buttonPressedAction }
}

MockButtonReleasedListener : MockActionListener {
	prSelector { ^\buttonReleasedAction }
}

MockTogglePressedListener : MockActionListener {
	prSelector { ^\togglePressedAction }
}

MockToggleReleasedListener : MockActionListener {
	prSelector { ^\toggleReleasedAction }
}

MockToggleValuePressedListener : MockActionListener {
	prSelector { ^\toggleValuePressedAction }
}

MockToggleValueReleasedListener : MockActionListener {
	prSelector { ^\toggleValueReleasedAction }
}

MockLitView : GRView {
	*new { |parent, origin, numCols=nil, numRows=nil, enabled=true|
		^super.new(parent, origin, numCols, numRows, enabled).init;
	}

	init {
		isLitAtFunc = { |point| true }
	}
}

MockUnlitView : GRView {
	*new { |parent, origin, numCols=nil, numRows=nil, enabled=true|
		^super.new(parent, origin, numCols, numRows, enabled).init;
	}

	init {
		isLitAtFunc = { |point| false }
	}
}

MockOddColsLitView : GRView {
	*new { |parent, origin, numCols=nil, numRows=nil, enabled=true|
		^super.new(parent, origin, numCols, numRows, enabled).init;
	}

	init {
		isLitAtFunc = { |point| point.x % 2 == 1 }
	}
}

MockLitContainerView : GRContainerView {
	*new { |parent, origin, numCols=nil, numRows=nil, enabled=true, press_through=false|
		^super.new(parent, origin, numCols, numRows, enabled).init;
	}

	init {
		isLitAtFunc = { |point| true }
	}
}

MockOddColsLitContainerView : GRContainerView {
	*new { |parent, origin, numCols=nil, numRows=nil, enabled=true|
		^super.new(parent, origin, numCols, numRows, enabled).init;
	}

	init {
		isLitAtFunc = { |point| point.x % 2 == 1 }
	}
}

MockContainerViewSubclassThatActsAsAView : GRContainerView {
	*new { |parent, origin, numCols=nil, numRows=nil|
		^super.new(parent, origin, numCols, numRows, true, true).init;
	}

	init {
		var button1;
		var button2;
		actsAsView = true;
		button1 = GRButton.newDetached(1, 1);
		button2 = GRButton.newDetached(1, 1);
		this.prAddChild(button1, Point.new(3, 0), true);
		this.prAddChild(button2, Point.new(2, 1), true);
	}

	newDetached { |numCols, numRows|
		^this.new(nil, nil, numCols, numRows);
	}
}

MockController : GRController {
	var <viewButtonStateChangedNotifications, <viewLedRefreshedNotifications;
	var registerNotifications;

	*new { |numCols=nil, numRows=nil, view=nil, origin=nil, createTopViewIfNoneIsSupplier=true|
		^super.new(numCols, numRows, view, origin, createTopViewIfNoneIsSupplier).init;
	}

	init {
		this.resetViewButtonStateChangedNotifications;
		this.resetViewLedRefreshedNotifications;
		registerNotifications = false;
		this.refresh;
		registerNotifications = true;
	}

/*
	// TODO: just use emit_press
	def emulate_press(point)
		emit_press(point)
	end

	// TODO: just use emit_release
	def emulate_release(point)
		emit_release(point)
	end
*/

	resetViewButtonStateChangedNotifications {
		viewButtonStateChangedNotifications = []
	}

	resetViewLedRefreshedNotifications {
		viewLedRefreshedNotifications = []
	}

/*
	info {
		// TODO: remove from SC and Ruby
		"[Description of MockController Settings]"
	}
*/

	handleViewButtonStateChangedEvent { |point, pressed|
 		if (registerNotifications) {
			viewButtonStateChangedNotifications = viewButtonStateChangedNotifications.add(
				( point: point, pressed: pressed )
			);
		}
	}

	handleViewLedRefreshedEvent { |point, on|
 		if (registerNotifications) {
			viewLedRefreshedNotifications = viewLedRefreshedNotifications.add(
				( point: point, on: on )
			);
		}
	}
}

MockNotePressedListener : MockActionListener {
	prSelector { ^\notePressedAction }
}

MockNoteReleasedListener : MockActionListener {
	prSelector { ^\noteReleasedAction }
}

MockButtonValueChangedListener : MockActionListener {
	prCreateListener { |view|
		^{ |view, x, y, value| // TODO: change to just view in ruby: multibutton implicit
			notifications = notifications.add(
				( view: view, x: x, y: y, val: value ) // TODO: change to just view in test, change to val in test?
			)
		}
	}
	prSelector { ^\buttonValueChangedAction }
}

MockToggleValueChangedListener : MockActionListener {
	prCreateListener { |view|
		^{ |view, i, value| // TODO: change to just view in ruby: multibutton implicit
			notifications = notifications.add(
				( view: view, i: i, val: value ) // TODO: change to just view in test
			)
		}
	}
	prSelector { ^\toggleValueChangedAction }
}

AbstractMockListener {
	var notifications;

	*new { |view=nil|
		^super.new.init(view);
	}

	init { |view|
		if (view.notNil) {
			this.addListener(view)
		};
		this.clearNotifications;
	}

	wasNotifiedOf { |ary|
		^notifications == ary
	}

	wasNotifiedOfAnything {
		^notifications.isEmpty
	}

	clearNotifications {
		notifications = Array.new
	}
}

MockLedEventListener : AbstractMockListener {
	var view;
	var listener;

	addListener { |argView|
		// TODO raise "listener already set" if @listener
		view = argView;
		listener = { |source, point, on|
			notifications = notifications.add( ( source: source.id, point: point, on: on ) )
		};
		view.addLedEventListener(listener);
	}

	removeListener {
		view.removeLedEventListener(listener);
	}
}

MockButtonEventListener : AbstractMockListener {
	var view;
	var listener;

	addListener { |argView|
		// TODO raise "listener already set" if @listener
		view = argView;
		listener = { |point, pressed|
			notifications = notifications.add( ( point: point, pressed: pressed ) )
		};
		view.addButtonEventListener(listener);
	}

	removeListener {
		view.removeButtonEventListener(listener)
	}
}
