package com.cronutils.descriptor;

import java.util.ResourceBundle;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.Month;
import org.threeten.bp.format.TextStyle;

import com.cronutils.Function;
import com.cronutils.model.field.definition.DayOfWeekFieldDefinition;
import com.cronutils.model.field.definition.FieldDefinition;
import com.cronutils.model.field.expression.FieldExpression;
import com.cronutils.model.field.expression.On;

/*
* Copyright 2014 jmrozanec
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
class DescriptionStrategyFactory {

    private DescriptionStrategyFactory() {}

    /**
     * Creates description strategy for days of week
     * @param bundle - locale
     * @param expression - CronFieldExpression
     * @return - DescriptionStrategy instance, never null
     */
    public static DescriptionStrategy daysOfWeekInstance(final ResourceBundle bundle, final FieldExpression expression, final FieldDefinition definition) {
        
    	final Function<Integer, String> nominal = new Function<Integer, String>() {
          @Override
          public String apply(Integer integer) {
              int diff = definition instanceof DayOfWeekFieldDefinition ? DayOfWeek.MONDAY.getValue() - ((DayOfWeekFieldDefinition) definition).getMondayDoWValue().getMondayDoWValue() : 0;
              return DayOfWeek.of(integer + diff < 1 ? 7 : integer + diff).getDisplayName(TextStyle.FULL, bundle.getLocale());
          }
      };
        
        NominalDescriptionStrategy dow = new NominalDescriptionStrategy(bundle, nominal, expression);

        dow.addDescription(new Function<FieldExpression, String>() {
            @Override
            public String apply(FieldExpression fieldExpression) {
                if (fieldExpression instanceof On) {
                    On on = (On) fieldExpression;
                    switch (on.getSpecialChar().getValue()) {
                        case HASH:
                            return String.format("%s %s %s ", nominal.apply(on.getTime().getValue()), on.getNth(), bundle.getString("of_every_month"));
                        case L:
                            return String.format("%s %s %s ", bundle.getString("last"), nominal.apply(on.getTime().getValue()), bundle.getString("of_every_month"));
                        default:
                            return "";
                    }
                }
                return "";
            }
        });
        return dow;
    }

    /**
     * Creates description strategy for days of month
     * @param bundle - locale
     * @param expression - CronFieldExpression
     * @return - DescriptionStrategy instance, never null
     */
    public static DescriptionStrategy daysOfMonthInstance(final ResourceBundle bundle, final FieldExpression expression) {
        NominalDescriptionStrategy dom = new NominalDescriptionStrategy(bundle, null, expression);

        dom.addDescription(new Function<FieldExpression, String>() {
            @Override
            public String apply(FieldExpression fieldExpression) {
                if (fieldExpression instanceof On) {
                    On on = (On) fieldExpression;
                    switch (on.getSpecialChar().getValue()) {
                        case W:
                            return String.format("%s %s %s ", bundle.getString("the_nearest_weekday_to_the"), on.getTime().getValue(), bundle.getString("of_the_month"));
                        case L:
                            return bundle.getString("last_day_of_month");
                        case LW:
                            return bundle.getString("last_weekday_of_month");
                        default:
                            return "";
                    }
                }
                return "";
            }
        });
        return dom;
    }

    /**
     * Creates description strategy for months
     * @param bundle - locale
     * @param expression - CronFieldExpression
     * @return - DescriptionStrategy instance, never null
     */
    public static DescriptionStrategy monthsInstance(final ResourceBundle bundle, final FieldExpression expression) {
        return new NominalDescriptionStrategy(
                bundle,
            new Function<Integer, String>() {
                @Override
                public String apply(Integer integer) {
                    return Month.of(integer).getDisplayName(TextStyle.FULL, bundle.getLocale());
                }
            },
                expression
        );
    }

    /**
     * Creates nominal description strategy
     * @param bundle - locale
     * @param expression - CronFieldExpression
     * @return - DescriptionStrategy instance, never null
     */
    public static DescriptionStrategy plainInstance(ResourceBundle bundle, final FieldExpression expression) {
        return new NominalDescriptionStrategy(bundle, null, expression);
    }

    /**
     * Creates description strategy for hh:mm:ss
     * @param bundle - locale
     * @return - DescriptionStrategy instance, never null
     */
    public static DescriptionStrategy hhMMssInstance(ResourceBundle bundle, final FieldExpression hours,
                                                     final FieldExpression minutes, final FieldExpression seconds) {
        return new TimeDescriptionStrategy(bundle, hours, minutes, seconds);
    }
}
