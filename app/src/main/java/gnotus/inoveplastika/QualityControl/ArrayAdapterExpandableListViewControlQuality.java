package gnotus.inoveplastika.QualityControl;

public class ArrayAdapterExpandableListViewControlQuality /* extends BaseExpandableListAdapter */{

//
//    private Context mContext;
//    private HashMap<String, ArrayList<String>> listDataChild;
//    private List<DataModelQualityControl> listHeader;
//
//    public ArrayAdapterExpandableListViewControlQuality(Context context, List<DataModelQualityControl> dataModelBos, HashMap<String, ArrayList<String>> listDataChild)
//    {
//        this.mContext       = context;
//        this.listDataChild  = listDataChild;
//        this.listHeader     = dataModelBos;
//
//
//    }
//
//
//    @Override
//    public int getGroupCount() {
//        return listHeader.size();
//    }
//
//    @Override
//    public int getChildrenCount(int i)
//    {
//        ArrayList<String> list = listDataChild.get(listHeader.get(i).getBostamp());
//        return list.size();
//
//    }
//
//    @Override
//    public Object getGroup(int i) {
//        return listDataChild.get(i);
//    }
//
//    @Override
//    public Object getChild(int i, int i1)
//    {
//
//        return listDataChild.get(listHeader.get(i).getBostamp()).get(i1);
////        return this.listDataChild.get(this.listHeader.get(i)).get(i1);
//    }
//
//    @Override
//    public long getGroupId(int i) {
//        return i;
//    }
//
//    @Override
//    public long getChildId(int i, int i1) {
//        return i1;
//    }
//
//    @Override
//    public boolean hasStableIds() {
//        return false;
//    }
//
//    @Override
//    public View getGroupView(int position, boolean b, View convertView, ViewGroup viewGroup)
//    {
//        DataModelQualityControl data = listHeader.get(position);
//
//        ViewHolderItem viewHolder;
//
//        if (convertView == null) {
//
//            LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = infalInflater.inflate(R.layout.listview_bi5lin, null);
//
//            viewHolder = new ArrayAdapterExpandableListViewControlQuality.ViewHolderItem();
//
//            viewHolder.textView_Lin1  = convertView.findViewById(R.id.textView_lin1);
//            viewHolder.textView_Lin1b = convertView.findViewById(R.id.textView_lin1b);
//            viewHolder.textView_Lin2a = convertView.findViewById(R.id.textView_lin2a);
//            viewHolder.textView_Lin2b = convertView.findViewById(R.id.textView_lin2b);
//            viewHolder.textView_Lin3a = convertView.findViewById(R.id.textView_lin3a);
//            viewHolder.textView_Lin3b = convertView.findViewById(R.id.textView_lin3b);
//            viewHolder.textView_Lin4a = convertView.findViewById(R.id.textView_lin4a);
//            viewHolder.textView_Lin4b = convertView.findViewById(R.id.textView_lin4b);
//            viewHolder.textView_Lin5a = convertView.findViewById(R.id.textView_lin5a);
//            viewHolder.textView_Lin5b = convertView.findViewById(R.id.textView_lin5b);
//
//            convertView.setTag(viewHolder);
//
//        } else {
//
//            viewHolder = (ArrayAdapterExpandableListViewControlQuality.ViewHolderItem) convertView.getTag();
//        }
//
//        if (data != null)
//        {
//
//            viewHolder.textView_Lin1.setText("["+ data.getRef().trim()+"]");
//            viewHolder.textView_Lin1b.setText(data.getDesign().trim());
//            viewHolder.textView_Lin2a.setText("Data pedido: "+ data.getData_pedido().substring(0, 10));
//            viewHolder.textView_Lin2b.setText("Data receção: "+ data.getData().substring(0, 10));
//            viewHolder.textView_Lin3a.setText("VossoDoc: "+data.getVossodoc().trim());
//            viewHolder.textView_Lin3b.setText(data.getDocumento().trim());
//            viewHolder.textView_Lin4a.setText("Qtt: "+ retiraDecimais(Double.parseDouble(data.getQtt())));
//            viewHolder.textView_Lin4b.setText(data.getNome());
//            viewHolder.textView_Lin5a.setText("Lote Forn: "+ data.getLotefl());
//            viewHolder.textView_Lin5b.setText("Lote I9: "+ data.getLote());
//        }
//
//        return convertView;
//
//    }
//
//    @Override
//    public View getChildView(int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup)
//    {
//        final String data = listDataChild.get(listHeader.get(groupPosition).getBostamp()).get(childPosition);
//        final ArrayAdapterExpandableListViewControlQuality.ViewHolderItem viewHolder;
//
//        if (convertView == null) {
//
//            LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = infalInflater.inflate(R.layout.activity_control_qual, null);
//
//            viewHolder = new ArrayAdapterExpandableListViewControlQuality.ViewHolderItem();
//
//            viewHolder.txt_contr_qual  = convertView.findViewById(R.id.txt_contr_qual);
//
//            convertView.setTag(viewHolder);
//
//        }
//        else
//            viewHolder = (ArrayAdapterExpandableListViewControlQuality.ViewHolderItem) convertView.getTag();
//
//
//        if (data != null)
//            viewHolder.txt_contr_qual.setText(data);
//
//
//        return convertView;
//    }
//
//    @Override
//    public boolean isChildSelectable(int i, int i1) {
//        return true;
//    }
//
//    private static class ViewHolderItem {
//
//        TextView textView_Lin1;
//        TextView textView_Lin1b;
//        TextView textView_Lin2a;
//        TextView textView_Lin2b;
//        TextView textView_Lin3a;
//        TextView textView_Lin3b;
//        TextView textView_Lin4a;
//        TextView textView_Lin4b;
//        TextView textView_Lin5a;
//        TextView textView_Lin5b;
//        TextView txt_contr_qual;
//
//    }

}