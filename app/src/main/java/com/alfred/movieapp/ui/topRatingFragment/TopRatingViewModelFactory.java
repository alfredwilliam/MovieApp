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

package com.alfred.movieapp.ui.topRatingFragment;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.alfred.movieapp.data.MovieRepository;
import com.alfred.movieapp.ui.homeFragment.HomeViewModel;


public class TopRatingViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MovieRepository mRepository;
    private final String mSortCriteria;

    public TopRatingViewModelFactory(MovieRepository repository, String sortCriteria) {
        this.mRepository = repository;
        this.mSortCriteria = sortCriteria;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new TopRatingViewModel(mRepository, mSortCriteria);
    }
}
