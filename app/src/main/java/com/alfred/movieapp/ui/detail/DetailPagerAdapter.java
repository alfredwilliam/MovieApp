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

package com.alfred.movieapp.ui.detail;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.alfred.movieapp.ui.cast.CastFragment;
import com.alfred.movieapp.ui.info.InformationFragment;
import com.alfred.movieapp.ui.review.ReviewFragment;
import com.alfred.movieapp.ui.trailer.TrailerFragment;
import com.alfred.movieapp.utilities.Constant;

import static com.alfred.movieapp.utilities.Constant.CAST;
import static com.alfred.movieapp.utilities.Constant.INFORMATION;
import static com.alfred.movieapp.utilities.Constant.REVIEWS;
import static com.alfred.movieapp.utilities.Constant.TRAILERS;

public class DetailPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public DetailPagerAdapter(Context context, FragmentManager fm){
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case INFORMATION:
                return new InformationFragment();
            case TRAILERS:
                return new TrailerFragment();
            case CAST:
                return new CastFragment();
            case REVIEWS:
                return new ReviewFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return Constant.PAGE_COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return Constant.TAP_TITLE[position % Constant.PAGE_COUNT].toUpperCase();
    }
}
