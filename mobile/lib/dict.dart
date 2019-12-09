enum Lang {
	ptBR, enUS
}

enum Verb {
	title, intro,
	cfgTitle, cfgShowIntro, cfgThemeColor, cfgLocale,
	secFO, secRestrictions,
	btnSolve,
}

class Dict {
	static const langName = const {
		Lang.ptBR : "Português BR",
		Lang.enUS : "English"
	};
	static const Map<Verb, Map<Lang, String>> texts = const {
		Verb.title : {
			Lang.ptBR : 'Complicadex',
			Lang.enUS : 'Complicadex',
		},
		Verb.intro : {
			Lang.ptBR : "Para calcular o Simplex, digite a função objetivo e as restrições nos campos apropriados. Escolha uma letra para cada variável. Para a função objetiva, digite 'max' ou 'min' após o sinal de igual. Siga os exemplos para digitar tudo no formato correto. Serão consideradas, automaticamente, restrições de >= 0 para todas as variáveis.",
			Lang.enUS : "To calculate the Simplex, type in the objective function and restrictions. Choose one letter to identify each variable. In the objective function type 'max' or 'min' after the '=' sign. Follow the examples as to fill every field aproprietely. Restriction of type >= 0 will be assumed for all variables.",
		},
		Verb.cfgTitle : {
			Lang.ptBR : "Opções",
			Lang.enUS : "Settings",
		},
		Verb.cfgShowIntro : {
			Lang.ptBR : "Mostrar instruções",
			Lang.enUS : "Show instructions",
		},
		Verb.cfgThemeColor : {
			Lang.ptBR : "Tema",
			Lang.enUS : "Theme",
		},
		Verb.cfgLocale : {
			Lang.ptBR : "Idioma",
			Lang.enUS : "Language",
		},
		Verb.secFO : {
			Lang.ptBR : "Função Objetivo",
			Lang.enUS : "Objective function",
		},
		Verb.secRestrictions : {
			Lang.ptBR : "Restrições",
			Lang.enUS : "Restrictions",
		},
		Verb.btnSolve : {
			Lang.ptBR : "Resolver",
			Lang.enUS : "Solve",
		}
	};
}


// Txt dic = Txt();
// class Txt {
// 	static int locale = 0;
// 	static const Map<String, List<String>> txt = const {
// 		'title' : [
// 			'Complicadex - Cálculoa',
// 			'Simplex - Calc'
// 		],
// 		'instructions' : [
// 			'Digite a função objetivo e as restrições nos campos apropriados. Siga os exemplos para digitá-las no formato correto. Para a função objetiva, digite "max" ou "min" após o sinal de igual.',
// 			'Type in the objective function and restrictions'
// 		],
// 		'cfg title' : [
// 			'Opções',
// 			'Settings',
// 		],
// 		'cfg examples' : [
// 			'Mostrar exemplos',
// 			'Show examples',
// 		],
// 		'cfg instructions' : [
// 			'Mostrar instruções',
// 			'Show instructions',
// 		],
// 		'section fo' : [
// 			'Função Objetivo',
// 			'Objective function',
// 		]
// 	};
// 	static String text(String id) => txt[id][locale];

// 	static String TITLE = txt['title'][locale];
// 	static String INSTRUCTIONS = txt['instructions'][locale];
// 	static String CFG_TITLE = txt['cfg title'][locale];
// 	static String CFG_EXAMPLES = txt['cfg examples'][locale];
// 	static String CFG_INSTRUCTIONS = txt['cfg instructions'][locale];
// 	static String SECTION_FO = txt['section fo'][locale];
// }
