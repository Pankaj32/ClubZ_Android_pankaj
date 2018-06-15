package com.clubz.ui.cv.chipview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clubz.R;

import java.util.ArrayList;
import java.util.List;

public class TagView extends LinearLayout {

    private final LinearLayout.LayoutParams TAG_VIEW_LAYOUT_PARAM = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
    private final LinearLayout.LayoutParams SPACE_LAYOUT_PARAM = new LinearLayout.LayoutParams(5, LinearLayout.LayoutParams.WRAP_CONTENT);
    private final List<SimpleChipView> textViews = new ArrayList<>();
    private int tagCount = -1;
    private Listner listner;

    public void setListner(Listner listner) {
        this.listner = listner;
    }

    public TagView(Context context) {
        super(context);
    }

    public TagView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TagView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TagView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        setOrientation(LinearLayout.HORIZONTAL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TagView);
        tagCount = typedArray.getInt(R.styleable.TagView_TagCount, 0);
        typedArray.recycle();
        bindViews();
    }

    private void bindViews() {
        textViews.clear();
        removeAllViews();
    }


    public interface Listner{
        void onDeleteChip(int size);
    }

    public void addTag(List<String> tagList){
        textViews.clear();
        removeAllViews();
        tagCount = tagList.size();

        for (int i = 0; i < tagCount; i++) {
            final SimpleChipView p = createTagView(tagList.get(i));
            textViews.add(p);
            addView(p);
            if ((i + 1) < tagCount) {
                addView(createSpace());
            }
        }
    }

    public void addTag(String tag){
       addTag(tag, false);
    }

    public void addTag(String tag, boolean isRemoviable){
        SimpleChipView p = createTagView(tag, isRemoviable);
        if(textViews.size()>0) addView(createSpace());
        textViews.add(p);
        addView(p);
        addView(createSpace());
    }

    public int size(){
        return textViews.size();
    }

    public ArrayList<String> getTags(){
        ArrayList<String> tags = new ArrayList<>();
        for(SimpleChipView tmp : textViews)
            tags.add(tmp.getTag());
        return tags;
    }

    public String getTagString(){
        String tags = "";
        for(SimpleChipView tmp : textViews){

            if(tags.isEmpty()){
                tags = tmp.getTag();
            }else {
                tags = String.format("%s,%s", tags, tmp.getTag());
            }
        }
        return tags;
    }

    private View createSpace() {
        View v = new View(getContext());
        v.setLayoutParams(SPACE_LAYOUT_PARAM);
        return v;
    }

    private SimpleChipView createTagView(String tag) {
        SimpleChipView p = new SimpleChipView(getContext(), false);
        p.setText(tag);
        p.setListner(new SimpleChipView.Listner() {
            @Override
            public void onDeleteBtnClick(SimpleChipView chipView) {
                textViews.remove(chipView);
                removeAllViews();
                tagCount = textViews.size();
                for(int i=0; i<textViews.size(); i++){
                    SimpleChipView tmp = textViews.get(i);
                    addView(tmp);
                    if ((i + 1) < tagCount) {
                        addView(createSpace());
                    }
                }

                if(listner!=null) listner.onDeleteChip(textViews.size());
            }
        });
        return p;
    }

    private SimpleChipView createTagView(String tag, boolean isRemoviable) {
        SimpleChipView p = new SimpleChipView(getContext(), isRemoviable);
        if(isRemoviable){
            p.setListner(new SimpleChipView.Listner() {
                @Override
                public void onDeleteBtnClick(SimpleChipView chipView) {
                    textViews.remove(chipView);
                    removeAllViews();
                    tagCount = textViews.size();
                    for(int i=0; i<textViews.size(); i++){
                        SimpleChipView tmp = textViews.get(i);
                        addView(tmp);
                        if ((i + 1) < tagCount) {
                            addView(createSpace());
                        }
                    }

                    if(listner!=null) listner.onDeleteChip(textViews.size());
                }
            });
        }
        p.setText(tag);
        return p;
    }

    public void distroy(){
        textViews.clear();
        removeAllViews();
    }
}
