delimiter $$

CREATE TABLE `plugin` (
  `idplugin` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(250) NOT NULL,
  `company` varchar(250) NOT NULL,
  `version` varchar(250) NOT NULL,
  `isPage` binary(1) NOT NULL DEFAULT '0',
  `deployIdentity` varchar(250) DEFAULT NULL,
  `role` varchar(250) NOT NULL,
  `externalservices` text,
  `icon` text,
  PRIMARY KEY (`idplugin`)
) ENGINE=InnoDB AUTO_INCREMENT=179 DEFAULT CHARSET=utf8$$

CREATE TABLE `user` (
  `staticId` bigint(20) NOT NULL,
  `temporaryId` varchar(250) NOT NULL,
  `expiration` datetime NOT NULL,
  `email` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`staticId`),
  UNIQUE KEY `UserIdentity_UNIQUE` (`temporaryId`),
  UNIQUE KEY `staticId_UNIQUE` (`staticId`),
  KEY `email_Index` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$

CREATE TABLE `script` (
  `idscript` int(11) NOT NULL AUTO_INCREMENT,
  `idplugin` int(11) NOT NULL,
  `script` text NOT NULL,
  `sortOrder` int(11) NOT NULL DEFAULT '0',
  `type` varchar(100) NOT NULL DEFAULT 'text/javascript',
  `pageName` varchar(250) NOT NULL,
  PRIMARY KEY (`idscript`),
  KEY `idplugin` (`idplugin`)
) ENGINE=InnoDB AUTO_INCREMENT=301 DEFAULT CHARSET=utf8$$

CREATE TABLE `page` (
  `idpage` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(250) NOT NULL,
  `plugin_idplugin` int(11) NOT NULL,
  PRIMARY KEY (`idpage`),
  UNIQUE KEY `idpage_UNIQUE` (`idpage`),
  KEY `plugin_pluginid_fk` (`plugin_idplugin`),
  CONSTRAINT `plugin_pluginid_fk` FOREIGN KEY (`plugin_idplugin`) REFERENCES `plugin` (`idplugin`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8$$

CREATE TABLE `menu` (
  `idmenu` int(11) NOT NULL AUTO_INCREMENT,
  `displayName` varchar(45) NOT NULL,
  `icon` text NOT NULL,
  `plugin_idplugin` int(11) NOT NULL,
  `reference` varchar(250) NOT NULL,
  `defaultPriority` int(11) NOT NULL,
  PRIMARY KEY (`idmenu`)
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8$$

CREATE TABLE `company` (
  `key` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `name` varchar(250) NOT NULL,
  PRIMARY KEY (`key`),
  UNIQUE KEY `companykey_UNIQUE` (`key`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$

CREATE TABLE `user_plugin` (
  `user_staticId` bigint(20) NOT NULL,
  `plugin_idplugin` int(11) NOT NULL,
  KEY `user_staticId_fk` (`user_staticId`),
  KEY `plugin_idplugin_fk` (`plugin_idplugin`),
  CONSTRAINT `plugin_idplugin_fk` FOREIGN KEY (`plugin_idplugin`) REFERENCES `plugin` (`idplugin`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `user_staticId_fk` FOREIGN KEY (`user_staticId`) REFERENCES `user` (`staticId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `addMenuItem`(displayName varchar(45), icon text, pluginId int, reference varchar(250), priority int)
BEGIN
    insert into menu (displayName, icon, plugin_idplugin, reference, defaultPriority)
    values (displayName, icon, pluginId, reference, priority);
    
    Select last_insert_id();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `addPage`(pageName varchar(250), pluginId int)
BEGIN
    insert into page (name, plugin_idplugin) values (pageName, pluginId);
    
    Select last_insert_id();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `addPlugin`(pluginName VARCHAR(250), pluginCompany VARCHAR(250), pluginVersion VARCHAR(250), pluginRole varchar(250), deployIdentity varchar(250), externalServices text)
BEGIN
    insert into plugin (name, company, version, role, deployIdentity, externalservices) 
    values (pluginName, pluginCompany, pluginVersion, pluginRole, deployIdentity, externalServices);
    
    select last_insert_id();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `addPluginToUser`(pluginId int, userStaticId bigint)
BEGIN
    insert into user_plugin (plugin_idplugin, user_staticId) values( pluginId, userStaticId);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `addScript`(pluginid int, scriptOrder int, scriptData text, scriptType varchar(100), pageName varchar(250))
BEGIN
    DECLARE itemid INT DEFAULT 1;
    
    insert into script (idplugin, sortOrder, script, type, pageName)
    values (pluginid, scriptOrder, scriptData, scriptType, pageName);
    
    set itemid = last_insert_id();
    
    select itemid;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deletePage`(pageId int)
BEGIN
    delete from page where idpage = pageId;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deletePlugin`(pluginid int)
BEGIN
    delete from page_script 
    where idPair <> 0 and idscript in (Select script.idscript from script where script.idplugin = pluginid);
    
    delete from menu where plugin_idplugin = pluginid and idmenu > 0;
    delete from script where idplugin = pluginid and idscript > 0;
    delete from page where plugin_idplugin = pluginid and idpage > 0;
    delete from plugin where idplugin = pluginid;
    
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteScript`(scriptId int)
BEGIN
    delete from script where idscript = scriptId;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getCompanyName`(companykey varchar(250))
BEGIN
    Select name from company where company.key = companykey;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getLogonData`(temporaryId varchar(250))
BEGIN
    Select staticId, expiration from user where user.temporaryId = temporaryId;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getPagePlugins`(staticUserId bigint, page varchar(250))
BEGIN
    Select plugin.* from plugin
    where
    (
        plugin.idplugin in (Select user_plugin.plugin_idplugin from user_plugin where user_plugin.user_staticId = staticUserId)
        or
        plugin.deployIdentity in (Select user.temporaryId from user where user.staticId = staticUserId)
    )
    and plugin.idplugin in
    (
        Select script.idplugin from script
        where script.pageName = page
    );
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getPages`(user_staticId bigint)
BEGIN
    Select page.* from page, plugin, user_plugin
    where plugin.idplugin = page.plugin_idplugin
    and deployIdentity = null
    and idplugin = user_plugin.plugin_idplugin
    union
    Select page.* from plugin, page
    where plugin.idplugin = page.plugin_idplugin
    and deployIdentity = user_staticId;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getPageScripts`(page varchar(250), staticUserId bigint)
BEGIN
    Select script.script as script, script.type as type from script
    where script.pageName = page
    and script.idplugin in 
    (
        Select plugin.idplugin from plugin, user_plugin
        where plugin.idplugin = user_plugin.plugin_idplugin
        and user_plugin.user_staticId = staticUserId
        union
        Select plugin.idplugin from plugin, user
        where plugin.deployIdentity = user.temporaryId
        and user.staticId = staticUserId
    );
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getPluginByCompanyNameVersionToken`(companyName varchar(250), pluginName varchar(250), version varchar(250), deployIdentity varchar(250))
BEGIN
    Select idplugin from plugin
    where plugin.name = pluginName
    and plugin.company = companyName
    and plugin.version = version
    and plugin.deployIdentity = deployIdentity;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getPlugins`()
BEGIN
    Select * from plugin where deployIdentity = null;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getUserIdFromEmail`(email varchar(250))
BEGIN
    Select staticId from user where user.email = email;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getUserIdFromToken`(token varchar(250))
BEGIN
    Select staticId from user where user.temporaryId = token;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getUserMenuItems`(userStaticId bigint)
BEGIN
    Select menu.* from menu, user_plugin
    where menu.plugin_idplugin = user_plugin.plugin_idplugin
    and user_plugin.user_staticId = userStaticId
    union
    Select menu.* from menu, user, plugin
    where plugin.deployIdentity = user.temporaryId
    and user.temporaryId is not null
    and menu.plugin_idplugin = plugin.idplugin
    and user.staticId = userStaticId;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getUserPlugins`(staticUserId NVARCHAR(250))
BEGIN
    Select plugin.* from plugin, user_plugin
    where plugin.idplugin = user_plugin.plugin_idplugin
    and user_plugin.user_staticId = staticUserId;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `removeTemporaryUserId`(temporaryId varchar(250))
BEGIN
    delete from user where user.temporaryId = temporaryId;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `setTemporaryUserId`(staticId bigint, temporaryId varchar(250), expiration datetime)
BEGIN
    delete from user where user.staticId = staticId;
    
    insert into user (staticId, temporaryId, expiration) values
    (staticId, temporaryId, expiration);
END$$

