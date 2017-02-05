package com.bluewatcher;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bluewatcher.app.generic.GenericAppConfig;

/**
 * @version $Revision$
 */
public class InstalledAppsListAdapter extends BaseAdapter {
    private List<ApplicationInfo> appsInfo;
    private PackageManager packageManager;
    private LayoutInflater inflator;

	@SuppressLint("DefaultLocale")
	public InstalledAppsListAdapter(LayoutInflater inflator, PackageManager packageManager, String filter) {
        super();
        this.inflator = inflator;
        this.packageManager = packageManager;
        if( filter == null ) {
        	this.appsInfo = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        } else {
        	List<ApplicationInfo> tempAppsInfo = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        	 this.appsInfo = new ArrayList<ApplicationInfo>();
        	 for( ApplicationInfo appInfo : tempAppsInfo ) {
        		 String appName = appInfo.loadLabel(packageManager).toString().toLowerCase();
        		 if(appName.contains(filter.toLowerCase()))
        			 appsInfo.add(appInfo);
        	 }
        }
    }
    
    public GenericAppConfig getApp(int position) {
    	ApplicationInfo appInfo = appsInfo.get(position);
    	return new GenericAppConfig(appInfo.packageName, appInfo.loadLabel(packageManager).toString());
    }

    @Override
    public int getCount() {
        return appsInfo.size();
    }

    @Override
    public Object getItem(int i) {
        return appsInfo.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("InflateParams")
	@Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (view == null) {
            view = inflator.inflate(R.layout.installed_apps_listitem, null);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) view.findViewById(R.id.installed_app_icon);
            viewHolder.name = (TextView) view.findViewById(R.id.installed_app_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        ApplicationInfo app = appsInfo.get(i);
        viewHolder.icon.setImageDrawable(app.loadIcon(packageManager));
        viewHolder.name.setText(app.loadLabel(packageManager));
        return view;
    }
    
    static class ViewHolder {
    	ImageView icon;
        TextView name;
    }
}