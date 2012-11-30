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
package net.nologin.meep.pingly.model;

import android.content.Context;
import net.nologin.meep.pingly.util.PinglyUtils;

/**
 * 'Day' Enum, providing translation key support
 */
public enum DayOfWeek {

    Monday(0),
    Tuesday(1),
    Wednesday(2),
    Thursday(3),
    Friday(4),
    Saturday(5),
    Sunday(6);

    public int id;

    private static String[] STRING_VALUES;

    DayOfWeek(int id){
        this.id = id;
    }

    public String getResourceNameForName(){
        return "day_of_week_" + id + "_name";
    }

    public static String[] toStringValueArray(Context ctx){

        if(STRING_VALUES == null){
            STRING_VALUES = PinglyUtils.enumToStringValuesArray(ctx, DayOfWeek.class,"getResourceNameForName");
        }
        return STRING_VALUES;
    }

    public static DayOfWeek fromId(int id){
        for(DayOfWeek t : DayOfWeek.values()){
            if(id == t.id){
                return t;
            }
        }
        throw new IllegalArgumentException("ID " + id  + " not a valid " + DayOfWeek.class.getSimpleName());
    }

    @Override
    public String toString(){
        return super.toString() + "[" + id + "]";
    }

}
