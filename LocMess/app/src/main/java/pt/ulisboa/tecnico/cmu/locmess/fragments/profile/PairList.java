package pt.ulisboa.tecnico.cmu.locmess.fragments.profile;

import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;

import pt.ulisboa.tecnico.cmu.locmess.MainActivity;
import pt.ulisboa.tecnico.cmu.locmess.R;

import java.util.ArrayList;
import java.util.List;

public class PairList implements AdapterView.OnItemLongClickListener{
    private View parent;
    private ListView list;
    private ArrayList<PairModel> pairs;
    private MenuItem deleteItem;
    private int selected = 0;

    public PairList(View view){
        this.parent = view;
        this.list = (ListView) parent.findViewById(R.id.list);

        this.pairs = new ArrayList<PairModel>();

        populate();
        adapter();
    }

    private void populate(){
        pairs.add(new PairModel("restaurante","capitanga"));
        pairs.add(new PairModel("clube","benfica"));
        pairs.add(new PairModel("cor","vermelho"));
        pairs.add(new PairModel("restaurante","capitanga"));
        pairs.add(new PairModel("clube","benfica"));
        pairs.add(new PairModel("cor","vermelho"));
        pairs.add(new PairModel("restaurante","capitanga"));
        pairs.add(new PairModel("clube","benfica"));
        pairs.add(new PairModel("cor","vermelho"));
        pairs.add(new PairModel("restaurante","capitanga"));
        pairs.add(new PairModel("clube","benfica"));
        pairs.add(new PairModel("cor","vermelho"));
        pairs.add(new PairModel("restaurante","capitanga"));
        pairs.add(new PairModel("clube","benfica"));
        pairs.add(new PairModel("cor","vermelho"));
        pairs.add(new PairModel("restaurante","capitanga"));
        pairs.add(new PairModel("clube","benfica"));
        pairs.add(new PairModel("cor","vermelho"));
    }

    public void setDeleteItem(MenuItem item){
        item.setVisible(false);
        this.deleteItem = item;

    }

    private void adapter(){
        list.setOnItemLongClickListener(this);
        PairAdapter adapter = new PairAdapter(parent.getContext(), (List) pairs);
        list.setAdapter(adapter);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position,long arg3) {

        if(!view.isSelected()) {
            view.setSelected(true);
            selected++;
        }else{
            view.setSelected(false);
            selected--;
        }
        deleteItem.setVisible(selected>0);

        return true;
    }

}
