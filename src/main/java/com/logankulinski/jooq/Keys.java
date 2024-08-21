/*
 * This file is generated by jOOQ.
 */
package com.logankulinski.jooq;


import com.logankulinski.jooq.tables.EsportsPick;
import com.logankulinski.jooq.tables.records.EsportsPickRecord;

import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in
 * public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<EsportsPickRecord> LOL_ESPORTS_PICK_PKEY = Internal.createUniqueKey(EsportsPick.ESPORTS_PICK, DSL.name("lol_esports_pick_pkey"), new TableField[] { EsportsPick.ESPORTS_PICK.GAME_ID }, true);
}
