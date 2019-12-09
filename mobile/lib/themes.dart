import 'package:flutter/material.dart';

class MyThemes {
	


	static var _colorChalkboard = Color.fromARGB( 255, 23, 30, 22 );
	static var _colorChalk = Color.fromARGB( 255, 185, 190, 180 );
	static var _colorChalkFaded = Color.fromARGB( 255, 100, 105, 95 );//255, 115, 125, 110 );

	static var bgImageAppbar = "img/math-overlay-tile.png";
	static var bgImageBody = [
		//1
		DecorationImage(
			alignment: Alignment.center,
			image: AssetImage( 'img/chalkboard.jpg' ),
			fit: BoxFit.none,
			repeat: ImageRepeat.repeat, 
		),
		//2
		DecorationImage(
			alignment: Alignment.center,
			image: AssetImage( 'img/paper-tile.png' ),
			fit: BoxFit.none,
			repeat: ImageRepeat.repeat, 
		),
	];

	static var porId = [
		// 0
		ThemeData(
			brightness: Brightness.light,
			primarySwatch: Colors.deepOrange,
			buttonTheme: ButtonThemeData(
				buttonColor: Colors.deepOrange,
				textTheme: ButtonTextTheme.primary
			),
			primaryTextTheme: TextTheme(
			),
			textTheme: TextTheme(
				body1: TextStyle( color: _colorChalk, fontSize: 15, height: 1.2, ),
				title: TextStyle( color: _colorChalk, ),
				subtitle: TextStyle( color: _colorChalk, ),
				subhead: TextStyle( color: _colorChalk, ),
			),
			canvasColor: _colorChalkboard,
			hintColor: _colorChalkFaded,
			dividerColor: _colorChalkboard,
			iconTheme: IconThemeData( color: _colorChalkFaded ),
		),
		// 1
		ThemeData(
			brightness: Brightness.light,
			primarySwatch: Colors.blueGrey,
			buttonTheme: ButtonThemeData(
				buttonColor: Colors.blueGrey,
				textTheme: ButtonTextTheme.primary
			),
			textTheme: TextTheme(
				body1: TextStyle( fontSize: 15, height: 1.5, ),
			),
			iconTheme: IconThemeData( color: Colors.grey, ),
		),
	
	];

}
