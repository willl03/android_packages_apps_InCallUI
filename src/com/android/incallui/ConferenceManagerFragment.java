/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.incallui;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.contacts.common.ContactPhotoManager;

import java.util.List;

/**
 * Fragment for call control buttons
 */
public class ConferenceManagerFragment
        extends BaseFragment<ConferenceManagerPresenter,
                ConferenceManagerPresenter.ConferenceManagerUi>
        implements ConferenceManagerPresenter.ConferenceManagerUi {

    private ListView mConferenceParticipantList;
    private int mActionBarElevation;
    private ContactPhotoManager mContactPhotoManager;
    private LayoutInflater mInflater;
    private ConferenceParticipantListAdapter mConferenceParticipantListAdapter;

    @Override
    ConferenceManagerPresenter createPresenter() {
        // having a singleton instance.
        return new ConferenceManagerPresenter();
    }

    @Override
    ConferenceManagerPresenter.ConferenceManagerUi getUi() {
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View parent =
                inflater.inflate(R.layout.conference_manager_fragment, container, false);

        mConferenceParticipantList = (ListView) parent.findViewById(R.id.participantList);
        mContactPhotoManager =
                ContactPhotoManager.getInstance(getActivity().getApplicationContext());
        mActionBarElevation =
                (int) getResources().getDimension(R.dimen.incall_action_bar_elevation);
        mInflater = LayoutInflater.from(getActivity().getApplicationContext());

        return parent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void setVisible(boolean on) {
        ActionBar actionBar = getActivity().getActionBar();
        boolean isDsdaEnabled = CallList.getInstance().isDsdaEnabled();
        if (on) {
            actionBar.setTitle(R.string.manageConferenceLabel);
            actionBar.setElevation(mActionBarElevation);
            if(!isDsdaEnabled) {
                actionBar.setHideOffset(0);
            }
            actionBar.show();

            final CallList calls = CallList.getInstance();
            getPresenter().init(getActivity(), calls);
            getView().setVisibility(View.VISIBLE);
            // Request focus on the list of participants for accessibility purposes.  This ensures
            // that once the list of participants is shown, the first participant is announced.
            mConferenceParticipantList.requestFocus();
        } else {
            getView().setVisibility(View.GONE);

            actionBar.setElevation(0);
            if(!isDsdaEnabled) {
                actionBar.setHideOffset(actionBar.getHeight());
            }
        }
    }

    @Override
    public boolean isFragmentVisible() {
        return isVisible();
    }

    @Override
    public void update(Context context, List<Call> participants, boolean parentCanSeparate) {
        if (mConferenceParticipantListAdapter == null) {
            mConferenceParticipantListAdapter = new ConferenceParticipantListAdapter(
                    mConferenceParticipantList, context, mInflater, mContactPhotoManager);

            mConferenceParticipantList.setAdapter(mConferenceParticipantListAdapter);
        }
        mConferenceParticipantListAdapter.updateParticipants(participants, parentCanSeparate);
    }

    @Override
    public void refreshCall(Call call) {
        mConferenceParticipantListAdapter.refreshCall(call);
    }
}
