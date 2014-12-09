package br.com.androiddit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import br.com.androiddit.data.Reddit;

/**
 * Created by gabrielbernardopereira on 12/1/14.
 */
public class RowAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<Reddit> entries;

    public RowAdapter(Context context, List values) {
        super(context, R.layout.row_layout, values);
        this.context = context;
        this.entries = values;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_layout, parent, false);
        TextView textViewTitle = (TextView) rowView.findViewById(R.id.title);
        textViewTitle.setText(entries.get(position).getTitle());

        TextView textViewAuthor = (TextView) rowView.findViewById(R.id.author);
        textViewAuthor.setText(entries.get(position).getAuthor());

        TextView textViewDate = (TextView) rowView.findViewById(R.id.creation_date);
        textViewDate.setText(entries.get(position).getHoursFromCreation().toString());

        TextView textViewNumberComments = (TextView) rowView.findViewById(R.id.number_comments);
        textViewNumberComments.setText(entries.get(position).getNumberOfComments().toString());

        ImageView imageView = (ImageView) rowView.findViewById(R.id.thumbnail);
        final String thumb = entries.get(position).getThumbnail();
        if (null != thumb && !thumb.isEmpty() && !thumb.equals("self") && !thumb.equals("default")) {
            try {
                Log.i("IMAGELOADD","thumb:"+thumb);

                ImageLoader.getInstance().displayImage(thumb, imageView);

                // Load image, decode it to Bitmap and return Bitmap to callback
//        imageLoader.loadImage(imageUri, new SimpleImageLoadingListener() {
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                // Do whatever you want with Bitmap
//            }
//        });
//        imageView.setImageResource(R.drawable.ic_launcher);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.setData(Uri.parse(thumb));
                        context.getApplicationContext().startActivity(i);
                    }
                });
            }catch (Exception e){}
        }else{
            ImageLoader.getInstance().displayImage("http://www.redditstatic.com/icon-touch.png", imageView);
        }
        return rowView;
    }
}