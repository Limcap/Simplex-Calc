import 'package:flutter/material.dart';
import './dict.dart';
import './themes.dart';
import './config.dart';





void main() async {
	await Config.loadPrefs();
	print( Config.locale );
	runApp( MyApp() );
}





class MyApp extends StatefulWidget {
	@override
	createState() => _MyAppState();
}
class _MyAppState extends State<MyApp> {
	_MyAppState() {
		Config.changeTheme = (int themeId) => setState( () { Config.themeId = themeId; Config.savePrefs(); } );
		Config.changeLocale = (Lang locale) => setState( () { Config.locale = locale; Config.savePrefs(); } );
	} 
	@override
	build( BuildContext context ) {
		return MaterialApp(
			title: Config.txt( Verb.title ),
			theme: MyThemes.porId[ Config.themeId ],
			home: MyHomePage(),
		);
	}
}





class MyHomePage extends StatefulWidget {
	MyHomePage({ Key key }) : super(key: key);
	@override
	_MyHomePageState createState() => _MyHomePageState();
}
class _MyHomePageState extends State<MyHomePage> {
	var _counter = 0;
	var pageController = PageController( initialPage: 0 );
	var crtlFuncaoObjetivo = TextEditingController();
	var crtlResticoes = TextEditingController();

	void _incrementCounter() {
		setState((){
			_counter -= 1;
		});
	}


	@override
	build(BuildContext context) {
		return Scaffold(
			appBar: myAppBar(),
			drawer: myDrawer(),
			body: body(), 
			floatingActionButton: floatingButton(),
		);
	}

	
	myAppBar() { /// in: global.cfg /// out: global.cfg
		var ab = AppBar(
			title: Text( Config.txt( Verb.title ), ),
			flexibleSpace: Container( decoration: bgAppbar(), child: null),
		);
		var mq = MediaQuery.of( context );
		Config.bodyHeight = mq.size.height - mq.padding.top - ab.preferredSize.height;
		Config.appBarHeight = ab.preferredSize.height + MediaQuery.of( context ).padding.top;
		return ab;
	}


	bgAppbar() => BoxDecoration(
		color: Theme.of(context).primaryColor,
		image: DecorationImage(
			alignment: Alignment.topLeft,
			image: AssetImage( MyThemes.bgImageAppbar ),
			fit: BoxFit.none,
			repeat: ImageRepeat.repeat, 
		),
	);


	myDrawer() => Drawer(
		child: Container(
			decoration: bgBody(),
			child: Column(
				crossAxisAlignment : CrossAxisAlignment.start,
				children : <Widget>[
					myDrawerAppbar(),
					myDrawerItem(
						myIcon: Icons.help,
						myVerb: Verb.cfgShowIntro,
						myWidget: Switch( value: Config.showIntro, onChanged: ( bool b ) {
							setState( () { Config.showIntro = b; } );
						}),
					),
					Divider(),
					myDrawerItem(
						myIcon: Icons.color_lens,
						myVerb: Verb.cfgThemeColor,
						myWidget: myThemeSelector( temas: MyThemes.porId, changeTheme: Config.changeTheme ),
					),
					Divider(),
					// myDrawerItem(
					// 	myIcon: Icons.language,
					// 	myVerb: Verb.cfgLocale,
					// 	myWidget: myLangSelector( Lang.values, Config.changeLocale, Config.locale ),
					// ),
					// Divider(),
					myDrawerItem(
						myIcon: Icons.language,
						myVerb: Verb.cfgLocale,
						myWidget: myLangSelector2( Lang.values, Config.changeLocale, Config.locale ),
					),
					Divider(),
				],
			),
		),
	);

	
	myDrawerAppbar() => Container(
		decoration: bgAppbar(),
		// color: Theme.of(context).primaryColor,
		height: Config.appBarHeight,
		margin: EdgeInsets.only(bottom: 10),
		alignment: Alignment.bottomLeft,
		child: Text( Config.txt( Verb.cfgTitle ), style: Theme.of(context).primaryTextTheme.title, ),
		padding: EdgeInsets.all(17),
	);


	myDrawerItem({@required IconData myIcon, @required Verb myVerb, @required Widget myWidget }) => Container(
		padding: EdgeInsets.all(15),
		child: Row(
			children: <Widget>[
				Icon( myIcon, ),
				SizedBox( width: 10, ),
				Expanded( child: Text( Config.txt( myVerb ), style: Theme.of( context ).textTheme.subtitle, ), ),
				myWidget,
			],
		),
	);


	myThemeSelector({@required List<ThemeData> temas, @required Function changeTheme }) {
		var w = List<Widget>();
		for( int i = 0; i < temas.length; i++ ) {
			w.add( _myThemeSelectorBox( temas[i], i, changeTheme ) );
			w.add( SizedBox( width: i == temas.length-1 ? 10 : 2 ) );
		}
		return Row( children: w );
	}
	// myDrawerItemTheme( List<Widget> w ) {
	// 	for( int i = 0; i < w.length-1; i+2 ) {
	// 		w.insert( i+1, SizedBox( width: 2 ));
	// 	}
	// 	w.add( SizedBox( width: 10 ) );
	// 	// w.forEach( ( i ) => w.insert( w.indexOf( i ), SizedBox( width: 2 ) ) );
	// 	return Row( children: w );
	// }


	_myThemeSelectorBox( ThemeData tema, int indice, Function changeTheme ) => Container(height: 45, width: 45,
		child: FlatButton(
			child: null,
			color: tema.primaryColor,
			onPressed: () => changeTheme( indice ),
		),
	);


	myLangSelector2( List<Lang> langs, Function changeLocale, Lang current ) {
		return ButtonTheme(
			alignedDropdown: true,
			child: DropdownButton<Lang>(
      		value: current,
				icon: Icon( Icons.arrow_drop_down, color: Theme.of(context).primaryColor ),
				style: Theme.of(context).textTheme.body1,
				underline: Container(),
				onChanged: ( Lang newLocale ) => changeLocale( newLocale ),
				items: langs.map<DropdownMenuItem<Lang>>(
					(Lang lang) => DropdownMenuItem<Lang>(
						value: lang,
						child: SizedBox(
							width: 95,
							child: Text( Dict.langName[lang], textAlign: TextAlign.right,),
						),
					)
				).toList(),
			)
		);
	}


	body() => SingleChildScrollView(
		child: Container(
			constraints: BoxConstraints( minHeight: Config.bodyHeight ),
			padding: EdgeInsets.fromLTRB( 18, Config.showIntro ? 0 : 18, 18, 0 ),
			decoration: bgBody(),
			child: Column(
				mainAxisAlignment: MainAxisAlignment.start,
				crossAxisAlignment: CrossAxisAlignment.start,
				children: <Widget>[
					SizedBox( height: 15, ),
					if( Config.showIntro ) ...cardInstrucoes(),
					// ...cardFuncaoObj(),
					// ...cardSistema(),
					...cardSistema(),
					//...cardResolucao(context),
					//cardTeclado(),
				],
			)
		)
	);


	bgBody() => BoxDecoration(
		color: Theme.of(context).canvasColor,
		image: MyThemes.bgImageBody[Config.themeId],
		// image: DecorationImage(
		// 	alignment: Alignment.topLeft,
		// 	image: AssetImage( Config.bgImageBody ),
		// 	fit: BoxFit.none,
		// 	repeat: ImageRepeat.repeat, 
		// ),
	);


	cardInstrucoes() => [
		Text( Config.txt( Verb.intro, ), textAlign: TextAlign.justify, ),//style: TextStyle( fontSize: 16, ), 
		SizedBox( height: 30, ),
	];


	// ignore: non_constant_identifier_names
	cardSistema() => [
		Text( Config.txt( Verb.secFO ), style: Theme.of(context).textTheme.title,),
   	TextField(
			controller: crtlFuncaoObjetivo,
			decoration: InputDecoration( hintText: Config.showIntro ? 'ex: 5x + 3y + 4z = max' : '', ),
			// keyboardType: TextInputType.number,
		),
		SizedBox( height: 30, ),
		Text( Config.txt( Verb.secRestrictions ), style: Theme.of(context).textTheme.title,),
		TextField(
			controller: crtlResticoes,
			decoration: InputDecoration( hintText: Config.showIntro ? 'ex: 2x + 4y + 8z >= 20\nex: 1x + 8y + 3z <= 14' : '' ),
			keyboardType: TextInputType.multiline,
			maxLines: null,
		),
		SizedBox( height: 30, ),
	];


	cardResolucao(BuildContext context) => [
		Text( 'Resolucao $_counter', style: Theme.of(context).textTheme.title, ),
		RaisedButton(
			child: Text( 'Calcular', ),
			onPressed: validateFO,			
		),
	];


	floatingButton() => FloatingActionButton(
		onPressed: _incrementCounter,
		tooltip: Config.txt( Verb.btnSolve ),
		child: Icon( Icons.play_arrow ),
	);


//==================================================================================================
//				LOGIC
//==================================================================================================


	void validateFO() {
		var reg = RegExp(r"^((\s*([0-9]+[a-z])\s*[+-])+\s*([0-9]+[a-z])\s*=\s*(max|min))$");
		var reg2 = RegExp(r"^(\s*[0-9]+[a-z])(\s*[+-]\s*[0-9]+[a-z])*(\s*=\s*(max|min))$");
		var str = crtlFuncaoObjetivo.text;
		//print( str );
		var g = reg.hasMatch( str );
		//print( g ? "Achou" : "no Matcha" );
		Iterable<Match> matches = reg2.allMatches(str);
		matches.forEach( (a) => print(a.group(2)) );
	}



}





/*





	myLangSelector( List<Lang> langs, Function changeLocale, Lang current ) {
		return Row(children: <Widget>[
			...langs.map( (l) => InkWell(
					onTap: () => changeLocale(l),
					child: Container(
						padding: EdgeInsets.fromLTRB(15,0,10,0),
						child: Text(
							l.toString().substring(l.toString().indexOf('.')),//.replaceAll('Lang.', ''),
							style: TextStyle(
								color: current == l ? Theme.of(context).primaryColor : Theme.of(context).disabledColor,
								fontWeight: FontWeight.bold,
							),
						)
					),
				),
			),
		],);
	}





	myDrawerV1() => Container(
		decoration: bgBody(),
		child: Drawer(
			child: Column(
				crossAxisAlignment : CrossAxisAlignment.start,
				children : <Widget>[
					// AppBar( title: Text( Config.txt( Verb.cfgTitle ),  ), leading: Container(), ),
					Container(
						decoration: bgAppbar(),
						// color: Theme.of(context).primaryColor,
						height: Config.appBarHeight,
						margin: EdgeInsets.only(bottom: 10),
						alignment: Alignment.bottomLeft,
						child: Text( Config.txt( Verb.cfgTitle ), style: Theme.of(context).primaryTextTheme.title, ),
						padding: EdgeInsets.all(17),
					),
					Container(
						padding: EdgeInsets.all(15),
						child: Row(
							children: <Widget>[
								Icon( Icons.lightbulb_outline, color: Colors.grey, ),
								SizedBox( width: 10, ),
								Expanded( child: Text( Config.txt( Verb.cfgShowIntro ), style: Theme.of( context ).textTheme.subtitle, ), ),
								Switch( value: Config.showIntro, onChanged: ( bool b ) { setState( () { Config.showIntro = b; } ); } ),
							],
						),
					),
					Divider(),
					Container(
						padding: EdgeInsets.all( 15 ),
						child: Row(
							children : <Widget>[
								Icon( Icons.ac_unit, color: Colors.grey, ),
								SizedBox( width: 10, ),
								Expanded( child: Text( Config.txt( Verb.cfgThemeColor ), style: Theme.of(context).textTheme.subtitle, ), ),
								InkWell(
									child: Container(	height: 50, width: 50,
										decoration: BoxDecoration(
											color: Temas.tema1primaryColor,
											border: Border.all( color: Colors.black, width: 3 ),
										),
									),
									onTap: () { Temas.mudarTema( Temas.tema1 ); },
								),
								SizedBox( width: 2, ),
								InkWell(
									child: Container( height: 50, width: 50,
										decoration: BoxDecoration(
											color: Temas.tema2primaryColor,
											border: null,
										),
									),
									onTap: () { Temas.mudarTema( Temas.tema2 ); },
								),
								SizedBox( width: 10, ),
							]
						),
					),
					Divider(),
				],
			),
		),
	);





	cardTeclado() => Expanded(
		child: GridView.count(
			crossAxisCount: 8,
			scrollDirection: Axis.vertical,
			children: <Widget>[
				RaisedButton( child: Text('1'), onPressed: (){},),
				RaisedButton( child: Text('2'), onPressed: (){},),
				RaisedButton( child: Text('3'), onPressed: (){},),
				RaisedButton( child: Text('4'), onPressed: (){},),
				RaisedButton( child: Text('5'), onPressed: (){},),
				RaisedButton( child: Text('6'), onPressed: (){},),
				RaisedButton( child: Text('7'), onPressed: (){},),
				RaisedButton( child: Text('8'), onPressed: (){},),
				RaisedButton( child: Text('9'), onPressed: (){},),
				RaisedButton( child: Text('0'), onPressed: (){},),
				RaisedButton( child: Text('='), onPressed: (){},),
				RaisedButton( child: Text('>'), onPressed: (){},),
				RaisedButton( child: Text('<'), onPressed: (){},),
				RaisedButton( child: Text('+'), onPressed: (){},),
				RaisedButton( child: Text('-'), onPressed: (){},),
				RaisedButton( child: Text('<<'), onPressed: (){},),
				RaisedButton( child: Text('x'), onPressed: (){},),
				RaisedButton( child: Text('y'), onPressed: (){},),
				RaisedButton( child: Text('z'), onPressed: (){},),
				RaisedButton( child: Text('w'), onPressed: (){},),
				RaisedButton( child: Text('max'), onPressed: (){},),
				RaisedButton( child: Text('min'), onPressed: (){},),
			],
		),
	);





	// ignore: non_constant_identifier_names
	cardFuncaoObj() => Row( children: <Widget>[
		MyInputVar('0'),
		Text('x		 +		'),
		MyInputVar('0'),
		Text('y		 +		'),
		MyInputVar('0'),
		Text('z		 =		'),
		MyInputVar('0'),
	]);





class MyInputVar extends StatelessWidget {
	MyInputVar( this.name );
	final String name;
	@override
	Widget build(BuildContext context) {
		return Expanded(
			child: TextField(
				keyboardType:TextInputType.number,
				textAlign: TextAlign.end,
				decoration: InputDecoration(
					hintText: this.name,
				),
			),
		);
	}
}



*/
