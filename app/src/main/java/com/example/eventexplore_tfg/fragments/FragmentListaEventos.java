package com.example.eventexplore_tfg.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


import com.example.eventexplore_tfg.Data.Event;
import com.example.eventexplore_tfg.R;

import java.io.Serializable;
import java.util.List;

public class FragmentListaEventos extends Fragment {
    private List<Event> events;
    private static final String ARG_EVENTS = "events";

    public FragmentListaEventos() {

    }

    public static FragmentListaEventos newInstance(List<Event> events) {
        FragmentListaEventos fragment = new FragmentListaEventos();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENTS, (Serializable) events);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            events = (List<Event>) getArguments().getSerializable(ARG_EVENTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_eventos, container, false);
//        RecyclerView recyclerView = view.findViewById(R.id.RecyclerFragmentEvents);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        EventsAdapter_Company adapter = new EventsAdapter_Company(events, this);
//        recyclerView.setAdapter(adapter);
        return view;
    }
}
