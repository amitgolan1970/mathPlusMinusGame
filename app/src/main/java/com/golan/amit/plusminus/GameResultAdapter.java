package com.golan.amit.plusminus;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GameResultAdapter extends ArrayAdapter<GameResult> {

    Context context;
    List<GameResult> objects;


    public GameResultAdapter(Context context, int resource, List<GameResult> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    public GameResult getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        return super.getView(position, convertView, parent);
//        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();

        View view = convertView;
        PlaceHolder holder = null;

        //  if we currently don't have a View to reuse
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);

            view = layoutInflater.inflate(R.layout.custom_row, parent, false);

            holder = new PlaceHolder();
            holder.correctView = view.findViewById(R.id.tvCorrectId);
            holder.wrongView = view.findViewById(R.id.tvWrongId);
            holder.scoreView = view.findViewById(R.id.tvScoreId);
            holder.dateView = view.findViewById(R.id.tvDateId);
            holder.imageView = view.findViewById(R.id.iv);

            view.setTag(holder);

        } else {
            //  Otherwise use an existing view
            holder = (PlaceHolder) view.getTag();
        }

        //  Getting the data from the resource input (GameResult)

        GameResult temp = objects.get(position);

        //  Setup and re-use the same ImageView listener
        holder.imageView.setOnClickListener(PopupListener);
        Integer rowPosition = position;
        //  saving the poaition for the PopupListener
        holder.imageView.setTag(rowPosition);

        holder.correctView.setText(String.valueOf(temp.getCorrect()));
        holder.wrongView.setText(String.valueOf(temp.getWrong()));
        holder.scoreView.setText(String.valueOf(temp.getScore()));
        holder.dateView.setText(String.valueOf(temp.getCurr_datetime()));

        if (temp.getCorrect() > temp.getWrong()) {
            holder.imageView.setImageResource(R.mipmap.green_ok);
        } else {
            holder.imageView.setImageResource(R.mipmap.red_notok);
        }

        return view;
    }

    View.OnClickListener PopupListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Integer viewPositiong = (Integer)v.getTag();

            final GameResult gr = objects.get(viewPositiong);
            if(gr != null) {
                if (MainActivity.DEBUG) {
                    Log.i(MainActivity.DEBUGTAG, "object: " + gr.toString());
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PlusMinusDbHelper pmdh = new PlusMinusDbHelper(context);
                        pmdh.open();
                        try {
                            if(MainActivity.DEBUG) {
                                Log.i(MainActivity.DEBUGTAG, "got id: " + gr.getId());
                            }
                            pmdh.deleteRecordById((long) gr.getId());
                        } catch (Exception ed) {
                            Log.e(MainActivity.DEBUGTAG, "delete record exception");
                        }
                        pmdh.close();
//                        refreshMyAdapter();
                        //  can not refresh page from here ...
                    }
                });
                builder.setNegativeButton("לא", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setTitle("מחיקת רשומה");
                builder.setMessage("האם למחוק את הרשומה?");
                AlertDialog dlg = builder.create();
                dlg.show();
            }
        }
    };

    private static class PlaceHolder {
        ImageView imageView;
        TextView correctView;
        TextView wrongView;
        TextView scoreView;
        TextView dateView;
    }
}
