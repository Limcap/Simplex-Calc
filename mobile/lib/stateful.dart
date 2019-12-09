import 'package:flutter/material.dart';

class GenericStateful extends StatefulWidget {
	Widget w;
	
	State<StatefulWidget> createStateFromWidget( Widget widget ) {
		this.w = widget;
		return createState();
	}
	
	@override
	_GenericStatefulState createState() => _GenericStatefulState( this.w );
}

class _GenericStatefulState extends State<GenericStateful> {
	_GenericStatefulState( this.w );
	final Widget w;
	@override
	Widget build(BuildContext context) {
		return this.w;
  }
}

