package gnotus.inoveplastika;

import java.util.ArrayList;

public class ViewsBackStackManager {

    private String activeView                       = ""; // guarda a visualização lista/fragment atualmente carregada no ecrã

    private ArrayList<String> arrayListStack        = new ArrayList<>();

    public String getActiveView() {
        return activeView;
    }

    public void setActiveView(String activeView) {
        this.activeView = activeView;
    }


    public ArrayList<String> getViewStack()
    {
       return arrayListStack;
    }

    public void addViewToBackStack(String view)
    {
        if (view.equals(getLastViewOnStack())) return;

        else {
            arrayListStack.add(view);
            arrayListStack.trimToSize();
        }
    }

    public String getLastViewOnStack()
    {
        if (arrayListStack.size() == 0)
            return "";
        else
            return  arrayListStack.get(arrayListStack.size()-1);
    }


    public void removeLastViewOnStack()
    {
        this.arrayListStack.remove(this.arrayListStack.size()-1);
    }

    public void removeActiveViewFromStack() {
        if (this.getActiveView().equals(this.getLastViewOnStack())) this.removeLastViewOnStack();
    }

    public int getBackStackEntryCount() {
        return arrayListStack.size();
    }

    public String getBackStackEntryAt(int index) {

        if (index < 0) return "";

        if (arrayListStack.size() > index) return  arrayListStack.get(index);
        else return "";

    }

    public void removeBackStackEntryAt(int index) {
        this.arrayListStack.remove(index);
    }


}
