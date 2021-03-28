/*
 *  Copyright 2018 Soojeong Shin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.alfred.movieapp.utilities;

import android.content.Context;

import com.alfred.movieapp.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.alfred.movieapp.utilities.Constant.ZERO;


public class FormatUtils {


    public static String formatNumber(int number) {
        DecimalFormat decimalFormat = new DecimalFormat(Constant.PATTERN_FORMAT_NUMBER);
        return decimalFormat.format(number);
    }


    public static String formatCurrency(long number) {
        DecimalFormat decimalFormat = new DecimalFormat(Constant.PATTERN_FORMAT_CURRENCY);
        return decimalFormat.format(number);
    }


    public static String formatDate(String releaseDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                Constant.PATTERN_FORMAT_DATE_INPUT, Locale.getDefault());
        Date date = null;
        try {
            date = dateFormat.parse(releaseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat outputDateFormat = new SimpleDateFormat(
                Constant.PATTERN_FORMAT_DATE_OUTPUT, Locale.getDefault());
        return outputDateFormat.format(date);
    }

    public static String formatTime(Context context, int runtime) {
        long hours = TimeUnit.MINUTES.toHours(runtime);
        long minutes = runtime - TimeUnit.HOURS.toMinutes(hours);
        if (minutes == ZERO) {
            return String.format(Locale.getDefault(), context.getString(R.string.format_runtime_hours), hours);
        }
        return String.format(Locale.getDefault(), context.getString(R.string.format_runtime), hours, minutes);
    }
}
