package xx.erhuo.com;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentTest extends Fragment {
    public static final String BUNDLE_TITLE = "title";
    private View view;
    public static FragmentTest newInstance(String text){

        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TITLE,text);
        FragmentTest f = new FragmentTest();
        f.setArguments(bundle);
        return f;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String text = "";
        Bundle budle = getArguments();
        if(budle != null){
            text = budle.getString(BUNDLE_TITLE);
        }
        view = inflater.inflate(R.layout.fragment_test1,container,false);
        TextView tv_test1 = view.findViewById(R.id.tv_test1);
        tv_test1.setText(text);
        return view;
    }
}
