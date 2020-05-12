package com.example.toeflwordsearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.GridView;

import com.example.toeflwordsearch.R;
import android.util.Log;
public class AlphabetTextAdapter extends BaseAdapter {

    private final Context mContext;
    private final char[] alphabet;

    // 1
    public AlphabetTextAdapter(Context context, char[] alphabet) {
        this.mContext = context;
        this.alphabet = alphabet;
    }
    // 2
    @Override
    public int getCount() {
        return alphabet.length;
    }
    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }
    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final char cellCharacter = alphabet[position];
        TextView textView;
        Log.d("general", "AlphabetTextAdapter cellCarhacter: "+cellCharacter);
        if (convertView == null) {
            textView = new TextView(mContext);
            textView.setLayoutParams(new GridView.LayoutParams(90, 90));
            //final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            //convertView = layoutInflater.inflate(R.layout.linearlayout_cell, null);
        } else {
            textView = (TextView) convertView;
        }

        textView.setText(cellCharacter);
        return textView;
    }


}