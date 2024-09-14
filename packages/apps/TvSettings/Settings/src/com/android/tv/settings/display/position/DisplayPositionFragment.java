/* Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.tv.settings.display.position;

import android.content.Context;
import android.os.Bundle;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.TwoStatePreference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.SeekBarPreference;
import android.util.Log;

import com.droidlogic.app.DisplayPositionManager;
import com.android.tv.settings.R;
import com.android.tv.settings.ConfigEnv;
import com.android.tv.settings.LeanbackAddBackPreferenceFragment;

public class DisplayPositionFragment extends LeanbackAddBackPreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "DisplayPositionFragment";

    private static final String SCREEN_POSITION_SCALE = "screen_position_scale";
    private static final String ZOOM_IN = "zoom_in";
    private static final String ZOOM_OUT = "zoom_out";
    private static final String ZOOM_OR_ALIGN = "zoom_or_alignment";
    private static final String SCREEN_ALIGNMENT = "screen_alignment";
    private static final String ALIGNMENT_LEFT = "alignment_left";
    private static final String ALIGNMENT_TOP = "alignment_top";
    private static final String ALIGNMENT_RIGHT = "alignment_right";
    private static final String ALIGNMENT_BOTTOM  = "alignment_bottom";

    private DisplayPositionManager mDisplayPositionManager;

    private PreferenceCategory zoomPref;
    private PreferenceCategory alignPref;
    private Preference zoominPref;
    private Preference zoomoutPref;
    private TwoStatePreference zoomOrAlign;
    private SeekBarPreference alignLeft;
    private SeekBarPreference alignTop;
    private SeekBarPreference alignRight;
    private SeekBarPreference alignBottm;

    int left, right, top, bottom;

    public static DisplayPositionFragment newInstance() {
        return new DisplayPositionFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.display_position, null);
        mDisplayPositionManager = new DisplayPositionManager((Context)getActivity());

        zoomOrAlign = (TwoStatePreference) findPreference(ZOOM_OR_ALIGN);
        zoomPref = (PreferenceCategory) findPreference(SCREEN_POSITION_SCALE);
        alignPref = (PreferenceCategory) findPreference(SCREEN_ALIGNMENT);

        zoominPref  = (Preference) findPreference(ZOOM_IN);
        zoomoutPref = (Preference) findPreference(ZOOM_OUT);

        alignLeft = (SeekBarPreference) findPreference(ALIGNMENT_LEFT);
        alignTop = (SeekBarPreference) findPreference(ALIGNMENT_TOP);
        alignRight = (SeekBarPreference) findPreference(ALIGNMENT_RIGHT);
        alignBottm = (SeekBarPreference) findPreference(ALIGNMENT_BOTTOM);

        alignLeft.setOnPreferenceChangeListener(this);
        alignTop.setOnPreferenceChangeListener(this);
        alignRight.setOnPreferenceChangeListener(this);
        alignBottm.setOnPreferenceChangeListener(this);

        zoomOrAlign.setChecked(!ConfigEnv.getAdjustScreenWay().equals("zoom"));

        int[] alignment = ConfigEnv.getScreenAlignment();
        left = alignment[0];
        top = alignment[1];
        right = alignment[2];
        bottom = alignment[3];

        updateMainScreen();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        switch (preference.getKey()) {
            case ALIGNMENT_LEFT:
                left = Integer.valueOf(value.toString());
                break;
            case ALIGNMENT_TOP:
                top = Integer.valueOf(value.toString());
                break;
            case ALIGNMENT_RIGHT:
                right = Integer.valueOf(value.toString());
                break;
            case ALIGNMENT_BOTTOM:
                bottom = Integer.valueOf(value.toString());
                break;
        }

        ConfigEnv.setScreenAlignment(left, top, right, bottom);
        mDisplayPositionManager.alignBy(left, top, right, bottom);
        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch (preference.getKey()) {
            case ZOOM_IN:
                mDisplayPositionManager.zoomIn();
                break;
            case ZOOM_OUT:
                mDisplayPositionManager.zoomOut();
                break;
        }
        updateMainScreen();
        return true;
    }

    private void updateMainScreen() {
        boolean zoom, align;
        if (!zoomOrAlign.isChecked()) {
            zoom = true;
            align = false;

            int percent = mDisplayPositionManager.getCurrentRateValue();
            zoomPref.setTitle("current scaling is " + percent +"%");
            ConfigEnv.setDisplayZoom(percent);
            ConfigEnv.setAdjustScreenWay("zoom");
        } else {
            zoom = false;
            align = true;
            ConfigEnv.setAdjustScreenWay("alignment");
            alignLeft.setValue(left);
            alignTop.setValue(top);
            alignRight.setValue(right);
            alignBottm.setValue(bottom);
        }
        zoomPref.setVisible(zoom);
        zoominPref.setVisible(zoom);
        zoomoutPref.setVisible(zoom);
        alignPref.setVisible(align);
        alignLeft.setVisible(align);
        alignTop.setVisible(align);
        alignRight.setVisible(align);
        alignBottm.setVisible(align);
    }
}
