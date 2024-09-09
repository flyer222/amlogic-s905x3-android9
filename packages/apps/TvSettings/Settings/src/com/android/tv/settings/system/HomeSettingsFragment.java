package com.android.tv.settings.system;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;

import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.tv.settings.search.BaseSearchIndexProvider;
import com.android.tv.settings.search.Indexable;
import java.util.ArrayList;
import java.util.Iterator;



public class HomeSettingsFragment extends SettingsPreferenceFragment implements Indexable {

	private static final String EXTRA_SUPPORT_MANAGED_PROFILES = "support_managed_profiles";
	
    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { 
	
    };
    private ComponentName[] mHomeComponentSet;
    private PackageManager mPm;
    private PreferenceGroup mPrefGroup;
    private ArrayList<HomeAppPreference> mPrefs;
    private boolean mShowNotice;
    private HomeAppPreference mCurrentHome = null;
    private HomePackageReceiver mHomePackageReceiver = new HomePackageReceiver();
	
    View.OnClickListener mHomeClickListener = new View.OnClickListener() { 
        public void onClick(View view) {
            HomeAppPreference homeAppPreference = (HomeAppPreference) HomeSettingsFragment.this.mPrefs.get(((Integer) view.getTag()).intValue());
            if (homeAppPreference.isChecked) {
                return;
            }
            HomeSettingsFragment.this.makeCurrentHome(homeAppPreference);
        }
    };
	
    View.OnClickListener mDeleteClickListener = new View.OnClickListener() { 
        public void onClick(View view) {
            HomeSettingsFragment.this.uninstallApp((HomeAppPreference) HomeSettingsFragment.this.mPrefs.get(((Integer) view.getTag()).intValue()));
        }
    };
    private final IntentFilter mHomeFilter = new IntentFilter("android.intent.action.MAIN");

 
    public class HomeAppPreference extends Preference {
        ComponentName activityName;
        HomeSettingsFragment fragment;
        final ColorFilter grayscaleFilter;
        int index;
        boolean isChecked;
        boolean isSystem;
        String uninstallTarget;

        public HomeAppPreference(Context context, ComponentName componentName, int i, Drawable drawable, CharSequence charSequence, HomeSettingsFragment homeSettingsFragment, ActivityInfo activityInfo, boolean z, CharSequence charSequence2) {
            super(context);
            setLayoutResource(R.layout.preference_home_app);
            setIcon(drawable);
            setTitle(charSequence);
            setEnabled(z);
            setSummary(charSequence2);
            this.activityName = componentName;
            this.fragment = homeSettingsFragment;
            this.index = i;
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(0.0f);
            colorMatrix.getArray()[18] = 0.5f;
            this.grayscaleFilter = new ColorMatrixColorFilter(colorMatrix);
            determineTargets(activityInfo);
        }

        private void determineTargets(ActivityInfo activityInfo) {
            String string;
            Bundle bundle = activityInfo.metaData;
            if (bundle != null && (string = bundle.getString("android.app.home.alternate")) != null) {
                try {
                    if (HomeSettingsFragment.this.mPm.checkSignatures(activityInfo.packageName, string) >= 0) {
                        PackageInfo packageInfo = HomeSettingsFragment.this.mPm.getPackageInfo(string, 0);
                        this.isSystem = (packageInfo.applicationInfo.flags & 1) != 0;
                        this.uninstallTarget = packageInfo.packageName;
                        return;
                    }
                } catch (Exception e) {
                    Log.w("HomeSettingsFragment", "Unable to compare/resolve alternate", e);
                }
            }
            this.isSystem = (activityInfo.applicationInfo.flags & 1) != 0;
            this.uninstallTarget = activityInfo.packageName;
        }

        @Override // android.support.v7.preference.Preference
        public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
            super.onBindViewHolder(preferenceViewHolder);
            ((RadioButton) preferenceViewHolder.findViewById(R.id.home_radio)).setChecked(this.isChecked);
            Integer num = new Integer(this.index);
            ImageView imageView = (ImageView) preferenceViewHolder.findViewById(R.id.home_app_uninstall);
            if (this.isSystem) {
                imageView.setEnabled(false);
                imageView.setColorFilter(this.grayscaleFilter);
            } else {
                imageView.setEnabled(true);
                imageView.setOnClickListener(HomeSettingsFragment.this.mDeleteClickListener);
                imageView.setTag(num);
            }
            View findViewById = preferenceViewHolder.findViewById(R.id.home_app_pref);
            findViewById.setTag(num);
            findViewById.setOnClickListener(HomeSettingsFragment.this.mHomeClickListener);
        }

        void setChecked(boolean z) {
            if (z != this.isChecked) {
                this.isChecked = z;
                notifyChanged();
            }
        }
    }

    /* loaded from: classes.dex */
    private class HomePackageReceiver extends BroadcastReceiver {
        private HomePackageReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            HomeSettingsFragment.this.buildHomeActivitiesList();
        }
    }

    public HomeSettingsFragment() {
        this.mHomeFilter.addCategory("android.intent.category.HOME");
        this.mHomeFilter.addCategory("android.intent.category.DEFAULT");
    }

    public void buildHomeActivitiesList() {
       ArrayList<ResolveInfo> homeActivities = new ArrayList<ResolveInfo>();
        ComponentName currentDefaultHome  = mPm.getHomeActivities(homeActivities);

        Context context = getActivity();
        mCurrentHome = null;
        mPrefGroup.removeAll();
        mPrefs = new ArrayList<HomeAppPreference>();
        mHomeComponentSet = new ComponentName[homeActivities.size()];
        int prefIndex = 0;
        boolean supportManagedProfilesExtra =
                getActivity().getIntent().getBooleanExtra(EXTRA_SUPPORT_MANAGED_PROFILES, false);
        boolean mustSupportManagedProfile = hasManagedProfile()
                || supportManagedProfilesExtra;
        for (int i = 0; i < homeActivities.size(); i++) {
            final ResolveInfo candidate = homeActivities.get(i);
            final ActivityInfo info = candidate.activityInfo;
            ComponentName activityName = new ComponentName(info.packageName, info.name);
            mHomeComponentSet[i] = activityName;
            try {
                Drawable icon = info.loadIcon(mPm);
                CharSequence name = info.loadLabel(mPm);
                HomeAppPreference pref;

                if (mustSupportManagedProfile && !launcherHasManagedProfilesFeature(candidate)) {
                    pref = new HomeAppPreference(context, activityName, prefIndex,
                            icon, name, this, info, false /* not enabled */,
                            getResources().getString(R.string.home_work_profile_not_supported));
                } else  {
                    pref = new HomeAppPreference(context, activityName, prefIndex,
                            icon, name, this, info, true /* enabled */, null);
                }

                mPrefs.add(pref);
                mPrefGroup.addPreference(pref);
                if (activityName.equals(currentDefaultHome)) {
                    mCurrentHome = pref;
                }
                prefIndex++;
            } catch (Exception e) {
                Log.v("HomeSettingsFragment", "Problem dealing with activity " + activityName, e);
            }
        }

        if (mCurrentHome != null) {
            if (mCurrentHome.isEnabled()) {
                getActivity().setResult(Activity.RESULT_OK);
            }

            new Handler().post(new Runnable() {
               public void run() {
                   mCurrentHome.setChecked(true);
               }
            });
        }
    }

    private boolean hasManagedProfile() {
        Iterator it = ((UserManager) getActivity().getSystemService("user")).getProfiles(getActivity().getUserId()).iterator();
        while (it.hasNext()) {
            if (((UserInfo) it.next()).isManagedProfile()) {
                return true;
            }
        }
        return false;
    }

    private boolean launcherHasManagedProfilesFeature(ResolveInfo resolveInfo) {
        try {
            return versionNumberAtLeastL(getActivity().getPackageManager().getApplicationInfo(resolveInfo.activityInfo.packageName, 0).targetSdkVersion);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static HomeSettingsFragment newInstance() {
        return new HomeSettingsFragment();
    }

    private boolean versionNumberAtLeastL(int i) {
        return i >= 21;
    }

	@Override
    public int getMetricsCategory() {
        return MetricsEvent.MANAGE_APPLICATIONS;
    }

	void makeCurrentHome(HomeAppPreference newHome) {
        if (mCurrentHome != null) {
            mCurrentHome.setChecked(false);
        }
        newHome.setChecked(true);
        mCurrentHome = newHome;

        mPm.replacePreferredActivity(mHomeFilter, IntentFilter.MATCH_CATEGORY_EMPTY,
                mHomeComponentSet, newHome.activityName);

        getActivity().setResult(Activity.RESULT_OK);
    }

    @Override // android.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        buildHomeActivitiesList();
        if (i <= 10 || this.mCurrentHome != null) {
            return;
        }
        for (int i3 = 0; i3 < this.mPrefs.size(); i3++) {
            HomeAppPreference homeAppPreference = this.mPrefs.get(i3);
            if (homeAppPreference.isSystem) {
                makeCurrentHome(homeAppPreference);
                return;
            }
        }
    }

    @Override 
    public void onCreatePreferences(Bundle bundle, String str) {
        setPreferencesFromResource(R.xml.home_selection, null);
        this.mPm = getActivity().getPackageManager();
        this.mPrefGroup = (PreferenceGroup) findPreference("home");
        Bundle arguments = getArguments();
        boolean z = false;
        if (arguments != null && arguments.getBoolean("show", false)) {
            z = true;
        }
        this.mShowNotice = z;
    }

	

    @Override // com.android.tv.settings.SettingsPreferenceFragment, android.app.Fragment
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(this.mHomePackageReceiver);
    }

    @Override // android.support.v14.preference.PreferenceFragment, android.support.v7.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        int i = 0;
        while (true) {
            if (i >= this.mPrefs.size()) {
                break;
            }
            HomeAppPreference homeAppPreference = this.mPrefs.get(i);
            if (!homeAppPreference.isChecked && homeAppPreference == preference) {
                makeCurrentHome(homeAppPreference);
                break;
            }
            i++;
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.tv.settings.SettingsPreferenceFragment, android.app.Fragment
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addDataScheme("package");
        getActivity().registerReceiver(this.mHomePackageReceiver, intentFilter);
        buildHomeActivitiesList();
    }

    void uninstallApp(HomeAppPreference homeAppPreference) {
        Intent intent = new Intent("android.intent.action.UNINSTALL_PACKAGE", Uri.parse("package:" + homeAppPreference.uninstallTarget));
        intent.putExtra("android.intent.extra.UNINSTALL_ALL_USERS", false);
        startActivityForResult(intent, 10 + (homeAppPreference.isChecked ? 1 : 0));
    }
}
