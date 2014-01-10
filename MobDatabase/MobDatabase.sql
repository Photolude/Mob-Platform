SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `mob` DEFAULT CHARACTER SET utf8 ;
USE `mob` ;

-- -----------------------------------------------------
-- Table `mob`.`plugin`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `mob`.`plugin` (
  `idplugin` INT(11) NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(250) NOT NULL ,
  `company` VARCHAR(250) NOT NULL ,
  `version` VARCHAR(250) NOT NULL ,
  `isPage` BINARY(1) NOT NULL DEFAULT '0' ,
  `deployIdentity` VARCHAR(250) NULL DEFAULT NULL ,
  `role` VARCHAR(250) NOT NULL ,
  `externalservices` TEXT NULL DEFAULT NULL ,
  `icon` TEXT NULL DEFAULT NULL ,
  `description` TEXT NULL DEFAULT NULL ,
  `tags` VARCHAR(250) NULL DEFAULT NULL ,
  `priority` INT(11) NULL DEFAULT NULL ,
  `attributeBlob` TEXT NULL DEFAULT NULL ,
  `serviceCalls` TEXT NULL DEFAULT NULL ,
  PRIMARY KEY (`idplugin`) )
ENGINE = InnoDB
AUTO_INCREMENT = 397
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `mob`.`art`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `mob`.`art` (
  `idart` INT(11) NOT NULL AUTO_INCREMENT ,
  `plugin_idplugin` INT(11) NOT NULL ,
  `path` VARCHAR(250) NOT NULL ,
  `data` TEXT NOT NULL ,
  `contentType` VARCHAR(250) NOT NULL ,
  PRIMARY KEY (`idart`) ,
  INDEX `art_plugin_fk` (`plugin_idplugin` ASC) ,
  INDEX `art_path_index` (`path` ASC) ,
  CONSTRAINT `art_plugin_fk`
    FOREIGN KEY (`plugin_idplugin` )
    REFERENCES `mob`.`plugin` (`idplugin` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 132
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `mob`.`company`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `mob`.`company` (
  `key` VARCHAR(250) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `name` VARCHAR(250) NOT NULL ,
  PRIMARY KEY (`key`) ,
  UNIQUE INDEX `companykey_UNIQUE` (`key` ASC) ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `mob`.`datacall`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `mob`.`datacall` (
  `iddatacall` INT(11) NOT NULL AUTO_INCREMENT ,
  `page` VARCHAR(250) NOT NULL ,
  `method` VARCHAR(50) NOT NULL ,
  `uri` VARCHAR(250) NOT NULL ,
  `content` TEXT NULL DEFAULT NULL ,
  `contentType` VARCHAR(50) NULL DEFAULT NULL ,
  `pageVariable` VARCHAR(250) NOT NULL ,
  `idplugin` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`iddatacall`) ,
  INDEX `PageName_Index` (`page` ASC) ,
  INDEX `Plugin_FK` (`idplugin` ASC) ,
  CONSTRAINT `Plugin_FK`
    FOREIGN KEY (`idplugin` )
    REFERENCES `mob`.`plugin` (`idplugin` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 114
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `mob`.`menu`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `mob`.`menu` (
  `idmenu` INT(11) NOT NULL AUTO_INCREMENT ,
  `displayName` VARCHAR(45) NOT NULL ,
  `icon` TEXT NOT NULL ,
  `plugin_idplugin` INT(11) NOT NULL ,
  `reference` VARCHAR(250) NOT NULL ,
  `defaultPriority` INT(11) NOT NULL ,
  PRIMARY KEY (`idmenu`) )
ENGINE = InnoDB
AUTO_INCREMENT = 331
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `mob`.`page`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `mob`.`page` (
  `idpage` INT(11) NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(250) NOT NULL ,
  `plugin_idplugin` INT(11) NOT NULL ,
  PRIMARY KEY (`idpage`) ,
  UNIQUE INDEX `idpage_UNIQUE` (`idpage` ASC) ,
  INDEX `plugin_pluginid_fk` (`plugin_idplugin` ASC) ,
  CONSTRAINT `plugin_pluginid_fk`
    FOREIGN KEY (`plugin_idplugin` )
    REFERENCES `mob`.`plugin` (`idplugin` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 278
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `mob`.`script`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `mob`.`script` (
  `idscript` INT(11) NOT NULL AUTO_INCREMENT ,
  `idplugin` INT(11) NOT NULL ,
  `script` TEXT NOT NULL ,
  `sortOrder` INT(11) NOT NULL DEFAULT '0' ,
  `type` VARCHAR(100) NOT NULL DEFAULT 'text/javascript' ,
  `pageName` VARCHAR(250) NOT NULL ,
  `scriptName` VARCHAR(250) NOT NULL ,
  PRIMARY KEY (`idscript`) ,
  INDEX `idplugin` (`idplugin` ASC) )
ENGINE = InnoDB
AUTO_INCREMENT = 3039
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `mob`.`page_script`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `mob`.`page_script` (
  `idpage` INT(11) NOT NULL ,
  `idscript` INT(11) NULL DEFAULT NULL ,
  `idPair` INT(11) NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (`idPair`) ,
  INDEX `idpage_key` (`idpage` ASC) ,
  INDEX `idpluginscript_key` (`idscript` ASC) ,
  INDEX `idscript_key` (`idscript` ASC) ,
  INDEX `idscript_fk` (`idscript` ASC) ,
  CONSTRAINT `idscript_fk`
    FOREIGN KEY (`idscript` )
    REFERENCES `mob`.`script` (`idscript` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `mob`.`required_roles`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `mob`.`required_roles` (
  `roleName` VARCHAR(50) NOT NULL ,
  PRIMARY KEY (`roleName`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `mob`.`user`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `mob`.`user` (
  `staticId` BIGINT(20) NOT NULL ,
  `temporaryId` VARCHAR(250) NOT NULL ,
  `expiration` DATETIME NOT NULL ,
  `email` VARCHAR(250) NULL DEFAULT NULL ,
  PRIMARY KEY (`staticId`) ,
  UNIQUE INDEX `UserIdentity_UNIQUE` (`temporaryId` ASC) ,
  UNIQUE INDEX `staticId_UNIQUE` (`staticId` ASC) ,
  INDEX `email_Index` (`email` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `mob`.`user_plugin`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `mob`.`user_plugin` (
  `user_staticId` BIGINT(20) NOT NULL ,
  `plugin_idplugin` INT(11) NOT NULL ,
  INDEX `user_staticId_fk` (`user_staticId` ASC) ,
  INDEX `plugin_idplugin_fk` (`plugin_idplugin` ASC) ,
  CONSTRAINT `plugin_idplugin_fk`
    FOREIGN KEY (`plugin_idplugin` )
    REFERENCES `mob`.`plugin` (`idplugin` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `user_staticId_fk`
    FOREIGN KEY (`user_staticId` )
    REFERENCES `mob`.`user` (`staticId` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- procedure addArt
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `addArt`(pluginId int, artPath varchar(250), artData TEXT, contentType varchar(250))
BEGIN
    insert into art (plugin_idplugin, path, data, contentType)
    values (pluginId, artPath, artData, contentType);
    
    select last_insert_id();
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure addDataCall
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `addDataCall`(page varchar(250), method varchar(50), uri varchar(250), content text, contentType varchar(50), pageVariable varchar(250), idplugin int)
BEGIN
    insert into datacall (page, method, uri, content, contentType, pageVariable, idplugin)
    values (page, method, uri, content, contentType, pageVariable, idplugin);
    
    select last_insert_id();
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure addMenuItem
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `addMenuItem`(displayName varchar(45), icon text, pluginId int, reference varchar(250), priority int)
BEGIN
    insert into menu (displayName, icon, plugin_idplugin, reference, defaultPriority)
    values (displayName, icon, pluginId, reference, priority);
    
    Select last_insert_id();
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure addPage
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `addPage`(pageName varchar(250), pluginId int)
BEGIN
    insert into page (name, plugin_idplugin) values (pageName, pluginId);
    
    Select last_insert_id();
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure addPlugin
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `addPlugin`(pluginName VARCHAR(250), pluginCompany VARCHAR(250), pluginVersion VARCHAR(250), pluginRole varchar(250), tags varchar(250), deployIdentity varchar(250), externalServices text, serviceCalls text, description text, icon text, priority int, attributeBlob text)
BEGIN
    insert into plugin (name, company, version, role, deployIdentity, externalservices, serviceCalls, description, icon, tags, priority, attributeBlob) 
    values (pluginName, pluginCompany, pluginVersion, pluginRole, deployIdentity, externalServices, serviceCalls, description, icon, tags, priority, attributeBlob);
    
    select last_insert_id();
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure addPluginToUser
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `addPluginToUser`(pluginId int, userStaticId bigint)
BEGIN
    insert into user_plugin (plugin_idplugin, user_staticId) values( pluginId, userStaticId);
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure addScript
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `addScript`(pluginid int, scriptOrder int, scriptData text, scriptType varchar(100), pageName varchar(250), scriptName varchar(250))
BEGIN
    DECLARE itemid INT DEFAULT 1;
    
    insert into script (idplugin, sortOrder, script, type, pageName, scriptName)
    values (pluginid, scriptOrder, scriptData, scriptType, pageName, scriptName);
    
    set itemid = last_insert_id();
    
    select itemid;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure associateScriptToPage
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `associateScriptToPage`(pageid int, scriptid int)
BEGIN
    insert into page_script (idpage, idscript) values (pageid, scriptid);
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure deleteArt
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `deleteArt`(artId int)
BEGIN
    delete from art
    where idart = artId;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure deleteDataCall
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `deleteDataCall`(datacallId int)
BEGIN
    delete from datacall
    where iddatacall = datacallId;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure deleteMenuItem
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `deleteMenuItem`(menuItemId int)
BEGIN
    delete from menu
    where idmenu = menuItemId;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure deletePage
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `deletePage`(pageId int)
BEGIN
    delete from page where idpage = pageId;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure deletePlugin
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `deletePlugin`(pluginid int)
BEGIN
    delete from page_script 
    where idPair <> 0 and idscript in (Select script.idscript from script where script.idplugin = pluginid);
    
    delete from menu where plugin_idplugin = pluginid and idmenu > 0;
    delete from script where idplugin = pluginid and idscript > 0;
    delete from page where plugin_idplugin = pluginid and idpage > 0;
    delete from datacall where idplugin = pluginid and iddatacall > 0;
    delete from plugin where idplugin = pluginid;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure deleteScript
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `deleteScript`(scriptId int)
BEGIN
    delete from script where idscript = scriptId;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure getCompanyName
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `getCompanyName`(companykey varchar(250))
BEGIN
    Select name from company where company.key = companykey;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure getDataCall
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `getDataCall`(userStaticId bigint, page varchar(250))
BEGIN
    Select * from datacall
    where datacall.page = page
    and datacall.idplugin in (
        Select plugin_idplugin from user_plugin
        where user_staticId = userStaticId
        union
        Select idplugin from plugin, user
        where user.temporaryId = plugin.deployIdentity);
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure getLogonData
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `getLogonData`(temporaryId varchar(250))
BEGIN
    Select staticId, expiration from user where user.temporaryId = temporaryId;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure getPagePlugins
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `getPagePlugins`(staticUserId bigint, page varchar(250))
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
        or script.pageName = '*'
    );
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure getPageScripts
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `getPageScripts`(page varchar(250), staticUserId bigint)
BEGIN
    Select script.idscript as idscript, script.script as script, script.type as type, script.scriptName as scriptName, script.pageName as pageName from script
    where 
    (
        script.pageName = page
        or script.pageName = '*'
    )
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

DELIMITER ;

-- -----------------------------------------------------
-- procedure getPages
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `getPages`(user_staticId bigint)
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

DELIMITER ;

-- -----------------------------------------------------
-- procedure getPluginArt
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `getPluginArt`(pluginId int)
BEGIN
    Select * from art
    where plugin_idplugin = pluginId;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure getPluginArtByPath
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `getPluginArtByPath`(userToken varchar(250), role varchar(250), path varchar(250))
BEGIN
    Select art.* from art, user, user_plugin, plugin
    where user.temporaryId = userToken
    and user.staticId = user_plugin.user_staticId
    and (plugin.idplugin = user_plugin.plugin_idplugin
    or plugin.deployIdentity = userToken)
    and plugin.role = role
    and plugin.idplugin = art.plugin_idplugin
    and art.path = path
    limit 1;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure getPluginByCompanyNameVersionToken
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `getPluginByCompanyNameVersionToken`(companyName varchar(250), pluginName varchar(250), version varchar(250), deployIdentity varchar(250))
BEGIN
    Select idplugin from plugin
    where plugin.name = pluginName
    and plugin.company = companyName
    and plugin.version = version
    and 
    (
        (
            deployIdentity is not null
            and plugin.deployIdentity = deployIdentity
        )
        or
        (
            deployIdentity is null
            and plugin.deployIdentity is null
        )
    );
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure getPluginById
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `getPluginById`(pluginId int)
BEGIN
    Select * from plugin where idplugin = pluginId;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure getPluginByUserAndRole
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `getPluginByUserAndRole`(userToken varchar(250), role varchar(250))
BEGIN
    Select * from plugin
    where plugin.deployIdentity = userToken
    and plugin.role = role
    union
    Select plugin.* from user, user_plugin, plugin
    where user.temporaryId = userToken
    and user.staticId = user_plugin.user_staticId
    and plugin.idplugin = user_plugin.plugin_idplugin
    and plugin.role = role;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure getPluginDataCall
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `getPluginDataCall`(pluginid int)
BEGIN
    Select * from datacall where idplugin = pluginid;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure getPluginMenuItems
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `getPluginMenuItems`(pluginId int(11))
BEGIN
    Select * from menu
    where plugin_idplugin = pluginId
    order by defaultPriority;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure getPluginScripts
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `getPluginScripts`(pluginId int(11))
BEGIN
    Select * from script
    where idplugin = pluginId;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure getPlugins
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `getPlugins`(userTempId varchar(250))
BEGIN
    Select * from (
    Select * from plugin where deployIdentity is null
    union
    Select * from plugin where deployIdentity = userTempId
    ) A
    order by priority desc;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure getRequiredRoles
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `getRequiredRoles`()
BEGIN
    Select * from required_roles;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure getUserIdFromEmail
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `getUserIdFromEmail`(email varchar(250))
BEGIN
    Select staticId from user where user.email = email;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure getUserIdFromToken
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `getUserIdFromToken`(token varchar(250))
BEGIN
    Select staticId from user where user.temporaryId = token;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure getUserMenuItems
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `getUserMenuItems`(userStaticId bigint)
BEGIN
    Select * from
    (
    Select menu.* from menu, user_plugin
    where menu.plugin_idplugin = user_plugin.plugin_idplugin
    and user_plugin.user_staticId = userStaticId
    union
    Select menu.* from menu, user, plugin
    where plugin.deployIdentity = user.temporaryId
    and user.temporaryId is not null
    and menu.plugin_idplugin = plugin.idplugin
    and user.staticId = userStaticId
    ) A
    order by defaultPriority desc;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure getUserPlugins
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `getUserPlugins`(staticUserId NVARCHAR(250))
BEGIN
    Select plugin.* from plugin, user_plugin
    where plugin.idplugin = user_plugin.plugin_idplugin
    and user_plugin.user_staticId = staticUserId;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure removePluginFromUser
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `removePluginFromUser`(userStaticId bigint, pluginId int)
BEGIN
    delete from user_plugin
    where user_staticId = userStaticId
    and plugin_idplugin = pluginId;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure removeTemporaryUserId
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `removeTemporaryUserId`(temporaryId varchar(250))
BEGIN
    delete from user where user.temporaryId = temporaryId;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure setTemporaryUserId
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `setTemporaryUserId`(staticId bigint, temporaryId varchar(250), expiration datetime)
BEGIN
    update user
    set user.temporaryId = temporaryId
    where user.staticId = staticId;
    -- delete from user where user.staticId = staticId;    
    -- insert into user (staticId, temporaryId, expiration) values (staticId, temporaryId, expiration);
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure updatePlugin
-- -----------------------------------------------------

DELIMITER $$
USE `mob`$$
CREATE PROCEDURE `updatePlugin`(pluginId int(11), role varchar(250), externalServices TEXT, serviceCalls text, description TEXT, icon text, tags varchar(250), priority int, attributeBlob text)
BEGIN
    update plugin
    set plugin.role = role,
    plugin.externalservices = externalServices,
    plugin.description = description,
    plugin.icon = icon,
    plugin.tags = tags,
    plugin.priority = priority,
    plugin.attributeBlob = attributeBlob,
    plugin.serviceCalls = serviceCalls
    where idplugin = pluginId;
END$$

DELIMITER ;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
