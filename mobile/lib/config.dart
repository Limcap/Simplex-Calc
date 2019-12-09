import 'package:shared_preferences/shared_preferences.dart';
import './dict.dart';




class Config {
	// user preferences
	static var themeId;
	static var locale;
	static var showIntro;
	// data used by widgets
	static var bodyHeight;
	static var appBarHeight;
	// functions initialized in MyAppState since they need reference to setState
	static Function changeLocale;
	static Function changeTheme;
	// functions
	static txt( Verb v ) => Dict.texts[ v ][ locale ];
	static loadPrefs() async {
		final prefs = await SharedPreferences.getInstance();
		themeId = prefs.getInt( 'themeId' ) ?? 0;
		showIntro = prefs.getBool( 'showIntro' ) ?? true;
		locale = Lang.values.firstWhere(
			(l) => l.toString() == prefs.getString('locale'),
			orElse: () => Lang.enUS
		);
		//print('saved prefs: locale: $locale, themeId: $themeId, showIntro: $showIntro');
		print( "User Preferences:" );
		prefs.getKeys().forEach( (o) => print( "$o = " + prefs.get(o).toString()  ) );
		prefs.clear();
		savePrefs();
	}
	static savePrefs() async {
		final prefs = await SharedPreferences.getInstance();
		prefs.setInt('themeId', themeId );
		prefs.setString('locale', locale.toString() );
		prefs.setBool('showIntro', showIntro );
	}
}
