package com.example.shoppinglist2;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public  class StoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //i choose RecyclerView.ViewOlder because if have two types of ViewHolder- one for the category and one for the product so i have to take general holder.

    private HashMap<Category, ArrayList<Product>> products;//refer the store hash-map;
    MyAdapterListenr myAdapterListenr;// this listener define in main activity
    //those keys are used to find the correct indexs
    Set<Category> keys;

    /**
     * constructor that get an hash map and make set of keys.
     * the hash-map is my data structure that i used. is not recommended to use.
     * is better use an array list. is come more fit.
     * i made a lot adjustments to make it work
     * @param products- the @store reference from main activity
     */
    public StoreAdapter(HashMap<Category, ArrayList<Product>> products) {
        this.products = products;
        keys = products.keySet();

    }

    /**
     * MyAdapterListener is a set of methods that defined in the main activity file
     * boughtthisitem- bring the position of the item in the category and if user press on the switch
     * onclick- activate when the user make a long click- and should be an act to delete from the list
     * currentPosition- return the index number of the position
     */
    interface MyAdapterListenr{
        void boughtThisItem(Category c, int position, View view, boolean purchased);
        void onclick(Category c, int position, View view);
        void currentPosition(int position);
    }


    /**
     * this method are use to set the listener in the class that implements her and all the methods that come with her.
     * @param listener- the listener that we define
     */
    public void setMyAdapterListenr(MyAdapterListenr listener){
        myAdapterListenr = listener;
    }

    /**
     * calculate how much items there are in the list.
     * @return the number of items in the list
     */
    @Override
    public int getItemCount() {
        int sumOfItemToPresnt = products.keySet().size();
        Set<Category> keys = products.keySet();
        ArrayList<Product> tempArray= null;
        for (Category x:keys){
            tempArray = products.get(x);
            sumOfItemToPresnt= sumOfItemToPresnt+ tempArray.size();
        }
        return sumOfItemToPresnt;
    }

    /**
     * create or bring the view holder that fit to right card- category or product
     * @param viewGroup- the layout that is need to be inflated
     * @param i- the type of the viewHolder the list needed to show
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        if(i == 0){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_card_view,viewGroup,false);
            StoreViewHolder storeViewHolder = new StoreViewHolder(view);
            return storeViewHolder;
        }
        if (i==1){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_xml,viewGroup,false);
            CategoryViewHolder categoryViewHolder = new CategoryViewHolder(view);
            return categoryViewHolder;
        }
        return null;
    }

    /**
     * this method calculate which content to put the view holder- by the general position and the category
     * @param viewHolder
     * @param position-the next space that need to be inflate
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Product p =null;
        Category c = null;

        int num=0;
        for(Category k: products.keySet()){
            if(num == position){
                c = k;
                break;
            }
            if(position < num+k.getIndexNUMber()+1) {//position=8<10
               num = num+k.getIndexNUMber();//num=9
               int i = num- position;//i=1
               int j = k.indexNUMber-i-1;//4-1-1=2
               p = products.get(k).get(j);
               break;
            }
            num = num+ k.getIndexNUMber()+1;
        }
        if (viewHolder instanceof StoreViewHolder){
            ((StoreViewHolder) viewHolder).name.setText(p.getNameProduct());
            ((StoreViewHolder) viewHolder).aSwitch.setChecked(p.getPurchased());

        }
        if (viewHolder instanceof CategoryViewHolder){
            ((CategoryViewHolder) viewHolder).categoryName.setText(c.nameCategory);

        }


    }

    /**
     * get the next position and return the type of the item- category or product
     * check with hash map,
     * @param position- next position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        int num=0;
        int type =0;
        for(Category k: products.keySet()){
            if(num == position)
                return type=1;
            num = num+ k.getIndexNUMber()+1;
        }

        return type;
    }

    /**
     * example- if the number of general position is 15- and there 8 category and to which category there is a few product.
     * i want from the method to give me the position in specific category.
     * @param position-general position of the item in the adapter
     * @return the index of the product in category
     */
    public int getIndexOfKeyFromPosition(int position){
        int po = position;
        int index=0;
        for(Category k:products.keySet()) {
            if (position < index + k.getIndexNUMber() + 1) {//position=8<10
                index = index + k.getIndexNUMber();//num=9
                int i = index - position;//i=1
                index = k.indexNUMber - i - 1;//4-1-1=2
                break;
            }
            index = index+ k.getIndexNUMber()+1;
        }
        return index;
    }

    /**
     *
     * @param position of product in the list
     * @return the category that fit the position
     */
    public Category getCategoryFromPosition(int position){
        int po = position;
        int index=0;
        Category category =null;
        for(Category k:products.keySet()) {
            if (position < index + k.getIndexNUMber() + 1) {//position=8<10
               category =k;
                break;
            }
            index = index+ k.getIndexNUMber()+1;
        }
        return category;
    }

    /**
     * StoreViewHolder- refer to the card of the product
     * this is after he is inflate
     * i define here also the listener of long click or check change listener
     */
    public class StoreViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        Switch aSwitch;
        public StoreViewHolder(final View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.product_name);
            aSwitch = itemView.findViewById(R.id.is_bought);

            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    myAdapterListenr.boughtThisItem(getCategoryFromPosition(getAdapterPosition()),getIndexOfKeyFromPosition(getAdapterPosition()),
                                                    buttonView,isChecked);
                    myAdapterListenr.currentPosition(getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    myAdapterListenr.onclick(getCategoryFromPosition(getAdapterPosition()),getIndexOfKeyFromPosition(getAdapterPosition()),v);
                    myAdapterListenr.currentPosition(getAdapterPosition());
                    return false;
                }
            });
        }
    }

    /**
     * CategoryViewHolder class refer to the category
     */
    public class CategoryViewHolder extends RecyclerView.ViewHolder{
        TextView categoryName;
        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category);
        }
    }
}