package com.thoughtbot.expandablerecyclerview.singlecheck;

import ohos.agp.colors.RgbColor;
import ohos.agp.components.Button;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.Image;
import ohos.agp.components.ScrollView;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.utils.Color;
import ohos.app.Context;
import com.anshul.expandablelistview.ResourceTable;
import com.thoughtbot.expandablecheckrecyclerview.ExpandableListAdapter;
import com.thoughtbot.expandablecheckrecyclerview.ExpandableListContainer;
import com.thoughtbot.expandablecheckrecyclerview.util.ResUtil;
import com.thoughtbot.expandablerecyclerview.entities.ParentChild;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Helper class for SingleCheckAbilitySlice.
 */
public class SingleCheckHelper {
    private ExpandableListContainer mGroupContainer;
    private ArrayList<ParentChild> mGroupNameItem = new ArrayList<>();
    private ArrayList<String> mTempGroupNameItem = new ArrayList<>();
    private ArrayList<String> mTempChildNameItem = new ArrayList<>();
    private ArrayList<String> mFinalGroupNameItem = new ArrayList<>();
    private ArrayList<Integer> mGroupImageItem = new ArrayList<>();
    private HashMap<String, String> mSelectedChild = new HashMap<>();
    private Button clearbtn;
    private Context context;
    private ComponentContainer rootView;

    public SingleCheckHelper(Context context, ComponentContainer rootView) {
        this.context = context;
        this.rootView = rootView;
    }

    /**
     * This method is used to init view components.
     */
    public void initViews() {
        getGroupItems();
        getGroupIcons();
        clearbtn = (Button) rootView.findComponentById(ResourceTable.Id_clearbtn);
        ScrollView parentLayout = (ScrollView) rootView.findComponentById(ResourceTable.Id_root_singlecheck);
        parentLayout.setBackground(getShapeElement(ResUtil.getColor(context, ResourceTable.Color_white)));
        mGroupContainer = (ExpandableListContainer)
                rootView.findComponentById(ResourceTable.Id_lcGroupItems_singlecheck);
        prepareExpandableListAdapter();
    }

    /**
     * This method is used to prepare group items.
     */
    private void getGroupItems() {
        mGroupNameItem.add(new ParentChild(null, ResUtil.getString(context, ResourceTable.String_item_Rock)));
        mGroupNameItem.add(new ParentChild(null, ResUtil.getString(context, ResourceTable.String_item_Jazz)));
        mGroupNameItem.add(new ParentChild(null, ResUtil.getString(context, ResourceTable.String_item_Classic)));
        mGroupNameItem.add(new ParentChild(null, ResUtil.getString(context, ResourceTable.String_item_Salsa)));
        mGroupNameItem.add(new ParentChild(null, ResUtil.getString(context, ResourceTable.String_item_Bluegrass)));
        mFinalGroupNameItem.add(ResUtil.getString(context, ResourceTable.String_item_Rock));
        mFinalGroupNameItem.add(ResUtil.getString(context, ResourceTable.String_item_Jazz));
        mFinalGroupNameItem.add(ResUtil.getString(context, ResourceTable.String_item_Classic));
        mFinalGroupNameItem.add(ResUtil.getString(context, ResourceTable.String_item_Salsa));
        mFinalGroupNameItem.add(ResUtil.getString(context, ResourceTable.String_item_Bluegrass));
    }

    /**
     * This method is used to prepare group items images.
     */
    private void getGroupIcons() {
        mGroupImageItem.add(ResourceTable.Media_rock);
        mGroupImageItem.add(ResourceTable.Media_jazz);
        mGroupImageItem.add(ResourceTable.Media_classic);
        mGroupImageItem.add(ResourceTable.Media_salsa);
        mGroupImageItem.add(ResourceTable.Media_bluegrass);
    }

    /**
     * This method is used to prepare adapter for list data.
     */
    private void prepareExpandableListAdapter() {
        ExpandableListAdapter<ParentChild> expandableListAdapter = new ExpandableListAdapter<ParentChild>(context,
                mGroupNameItem, mGroupImageItem, ResourceTable.Layout_ability_listview_item) {
            @Override
            protected void bind(ViewHolder holder, ParentChild text, Integer image, int position) {
                if (!mTempChildNameItem.contains(text.getChildItem())) {
                    // Set green background for parent/Group
                    holder.makeInvisibleButton(ResourceTable.Id_checkbtn);
                    holder.setGroupItemBackground(ResourceTable.Id_groupContainer, ResourceTable.Color_white);
                    holder.setText(ResourceTable.Id_tvGroupTitle, text.getChildItem(), Color.GRAY,
                            ResUtil.getIntDimen(context, ResourceTable.Float_group_text_size));
                    holder.setGroupImage(ResourceTable.Id_ivGroupIcon, image, ShapeElement.RECTANGLE,
                            Image.ScaleMode.STRETCH, ResourceTable.Color_white);
                    if (!mTempGroupNameItem.contains(text.getChildItem())) {
                        // Set divider & arrow down icon
                        holder.setGroupImage(ResourceTable.Id_ArrowIcon, ResourceTable.Media_arrow_Down,
                                ShapeElement.OVAL, Image.ScaleMode.CENTER, ResourceTable.Color_white);
                    } else {
                        // Remove divider & arrow up icon
                        holder.setGroupImage(ResourceTable.Id_ArrowIcon, ResourceTable.Media_arrow_Up,
                                ShapeElement.OVAL, Image.ScaleMode.CENTER, ResourceTable.Color_white);
                    }
                } else {
                    // Add child items to list
                    holder.makeInvisibleImage(ResourceTable.Id_ArrowIcon);
                    holder.setText(ResourceTable.Id_tvGroupTitle, text.getChildItem(), Color.BLACK,
                            ResUtil.getIntDimen(context, ResourceTable.Float_child_text_size));
                    if (mSelectedChild.containsKey(text.getParentItem()) && mSelectedChild.get(text.getParentItem())
                            .equals(text.getChildItem())) {
                        holder.setChecked(ResourceTable.Id_checkbtn);
                    } else {
                        holder.setUnChecked(ResourceTable.Id_checkbtn);
                    }
                }
            }
        };
        mGroupContainer.setItemProvider(expandableListAdapter);

        expandableListAdapter.setOnItemClickListener((component, position) -> {
            ParentChild value = mGroupNameItem.get(position);
            String clickedItem = value.getChildItem();
            if (!mTempChildNameItem.contains(clickedItem)) {
                if (mTempGroupNameItem.contains(clickedItem)) {
                    int actualItemPosition = mFinalGroupNameItem.indexOf(clickedItem);
                    removeChildItems(actualItemPosition, position);
                    mTempGroupNameItem.remove(clickedItem);
                } else {
                    int actualItemPosition = mFinalGroupNameItem.indexOf(clickedItem);
                    addChildItems(actualItemPosition, clickedItem, position);
                    mTempGroupNameItem.add(clickedItem);
                }
                expandableListAdapter.setData(mGroupNameItem);
            } else {
                String parentGroup = value.getParentItem();
                if (mSelectedChild.containsKey(parentGroup)) {
                    mSelectedChild.remove(parentGroup);
                }
                mSelectedChild.put(parentGroup, clickedItem);
                expandableListAdapter.setData(mGroupNameItem);
            }
        });

        //To clear the all the selected items
        clearbtn.setClickedListener(component -> {
            mSelectedChild.clear();
            expandableListAdapter.setData(mGroupNameItem);
        });
    }

    /**
     * This method is used to add child items in list.
     *
     * @param actualPosition position of group item
     * @param clickedItem    name of clicked item
     */
    private void addChildItems(int actualPosition, String clickedItem, int itemPositionFromGroup) {
        String[] childItems = childItems().get(actualPosition);
        for (String item : childItems) {
            itemPositionFromGroup = itemPositionFromGroup + 1;
            mGroupNameItem.add(itemPositionFromGroup, new ParentChild(clickedItem, item));
            mTempChildNameItem.add(item);
            mGroupImageItem.add(itemPositionFromGroup, ResourceTable.Media_star);
        }
    }

    /**
     * This method is used to remove child item.
     *
     * @param position    position of group item
     * @param itemPositionFromGroup the position of the parent Item
     */
    private void removeChildItems(int position, int itemPositionFromGroup) {
        String[] items = childItems().get(position);
        for (String name : items) {
            mGroupNameItem.remove(itemPositionFromGroup + 1);
            mGroupImageItem.remove(itemPositionFromGroup + 1);
            mTempChildNameItem.remove(name);
        }
    }

    /**
     * This method is used to prepare map based on group item index.
     *
     * @return HashMap
     */
    private HashMap<Integer, String[]> childItems() {
        HashMap<Integer, String[]> map = new HashMap<>();
        map.put(0, new String[]{
                ResUtil.getString(context, ResourceTable.String_item_child_Rock1),
                ResUtil.getString(context, ResourceTable.String_item_child_Rock2),
                ResUtil.getString(context, ResourceTable.String_item_child_Rock3),
                ResUtil.getString(context, ResourceTable.String_item_child_Rock4)});
        map.put(1, new String[]{
                ResUtil.getString(context, ResourceTable.String_item_child_Jazz1),
                ResUtil.getString(context, ResourceTable.String_item_child_Jazz2),
                ResUtil.getString(context, ResourceTable.String_item_child_Jazz3)});
        map.put(2, new String[]{
                ResUtil.getString(context, ResourceTable.String_item_child_Classic1),
                ResUtil.getString(context, ResourceTable.String_item_child_Classic2),
                ResUtil.getString(context, ResourceTable.String_item_child_Classic3),
                ResUtil.getString(context, ResourceTable.String_item_child_Classic4)});
        map.put(3, new String[]{
                ResUtil.getString(context, ResourceTable.String_item_child_Salsa1),
                ResUtil.getString(context, ResourceTable.String_item_child_Salsa2),
                ResUtil.getString(context, ResourceTable.String_item_child_Salsa3),
                ResUtil.getString(context, ResourceTable.String_item_child_Salsa4)});
        map.put(4, new String[]{
                ResUtil.getString(context, ResourceTable.String_item_child_Bluegrass1),
                ResUtil.getString(context, ResourceTable.String_item_child_Bluegrass2),
                ResUtil.getString(context, ResourceTable.String_item_child_Bluegrass3),
                ResUtil.getString(context, ResourceTable.String_item_child_Bluegrass4)});
        return map;
    }

    /**
     * This method is used to prepare the background shape element based on color.
     *
     * @param color color for the shape element
     * @return ShapeElement
     */
    private ShapeElement getShapeElement(int color) {
        ShapeElement shapeElement = new ShapeElement();
        shapeElement.setShape(ShapeElement.RECTANGLE);
        shapeElement.setRgbColor(RgbColor.fromArgbInt(color));
        return shapeElement;
    }
}