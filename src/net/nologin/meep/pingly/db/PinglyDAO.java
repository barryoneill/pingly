/*
 *    Pingly - A simple app for checking for signs of life in hosts/services.
 *    Copyright 2012 Barry O'Neill
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.nologin.meep.pingly.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public abstract class PinglyDAO {

    /* private to prevent (further) misuse
       We're keeping a single instance of the helper in the application object
       so we don't offer any close() functionality here.
     */
    private PinglyDataHelper dataHelper;

    public PinglyDAO(PinglyDataHelper dataHelper) {
        this.dataHelper = dataHelper;
    }

    protected SQLiteDatabase getReadableDB(){
        return this.dataHelper.getReadableDatabase();
    }

    protected SQLiteDatabase getWriteableDB(){
        return this.dataHelper.getWritableDatabase();
    }

    protected Context getDataHelperContext(){
        return this.dataHelper.getDataHelperContext();
    }


}
