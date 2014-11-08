/**
 * FileAdmin
 * Copyright 2010 Youngli Inc. All rights reserved.
 * 
 * path: js-src/fa/help.act.js
 * author: lichunping/jarry
 * version: 0.9
 * date: 2010/06/15
 */
 
 /**
 * 帮助Action
 * 帮助模板与显示帮助信息
 * @author lichunping/jarry
 * 
 */
HelpAction = (function() {
	
	var helpClass = new Help();

	var HELP_PAGE = {
		index  : ''
	};

	var pageInit = function() {
	}

	var show  = function() {
		if (HELP_PAGE.index.length <= 0) {
			var url = 'help/index.html';
			var xhr = ajax.get(url, parseHelpJSON);
		} else {
			helpClass.setHelpHTML(HELP_PAGE.index);
			toggleMask();
			_showHelpArea();
		}
	}

	var _showHelpArea = function() {
		if (g('HelpArea')) {
			g('HelpArea').style.display = '';
		}
	}

	var _hideHelpArea = function() {
		if (g('HelpArea')) {
			g('HelpArea').style.display = 'none';
		}
	}

	var close = function() {
		_hideHelpArea();
		toggleMask();
	}

	var parseHelpJSON = function(xhr, responseText) {
		HELP_PAGE.index = responseText;
		helpClass.setHelpHTML(responseText);
		toggleMask();
		_showHelpArea();
	}

	return {
		pageInit : pageInit,
		show : show,
		close : close
	}
})();
 
HelpAction.pageInit();