package uk.co.matloob.indietracks2014;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: maq
 * Date: 20/07/2013
 * Time: 12:40
 * To change this template use File | Settings | File Templates.
 */
public class InfoFragment extends SherlockFragment {
    private static final String TAG = "InfoFragment";

    public static final String INFO_TYPE = "InformationType";
    public static final String GETTING_THERE = "Getting There";
    public static final String TAXIS = "Taxi numbers";
    public static final String ABOUT = "About Indietracks";

    static Map<String,Spanned> infoTextMap = new HashMap<String, Spanned>();
    StringBuffer gettingThere = new StringBuffer();
    StringBuffer taxis = new StringBuffer();
    StringBuffer about = new StringBuffer();
    {
        if (gettingThere.length() == 0) {
            gettingThere.append("<h2>Getting there</h2>").append("\n");
            gettingThere.append("<p />Indietracks takes place at the Midland Railway Centre, near Ripley in Derbyshire. It’s about 12 miles from Derby, 15 miles from Nottingham and 25 miles from Sheffield.").append("\n");
            gettingThere.append("<p />There are two entrances to the Indietracks festival site.").append("\n");
            gettingThere.append("<p />").append("\n");
            gettingThere.append("<p />The main entrance for visitors is at Butterley Station, Butterley Hill, Ripley DE5 3QZ.  From here you can catch regular trains (including steam trains!) to the festival site. There is an ample car park for visitors at this entrance.").append("\n");
            gettingThere.append("<p />There is an alternative entrance at Swanwick Junction. If you are staying at the Golden Valley Campsite (DE55 4ES) or arriving on the H1 bus, this entrance is a 10-15 minute walk from the campsite. There is no public parking or access to visitors’ cars at this entrance.").append("\n");
            gettingThere.append("</p />").append("\n");
            gettingThere.append("<h3>By Road</h3>").append("\n");
            gettingThere.append("<p />The Midland Railway Centre is on the B6179, one mile north of Ripley, signposted from Ripley town centre, and from the A38, M1 junction 28. For sat navs, enter the address Butterley Station, Butterley Hill, Ripley DE5 3QZ. There is ample free parking.").append("\n");
            gettingThere.append("<h3>By Train or Bus</h3>").append("\n");
            gettingThere.append("<p />The nearest train stations are Derby, Nottingham and Alfreton. Details of bus connections from train stations to the festival are below under Local Transport.").append("\n");
            gettingThere.append("<h3>By air?</h3>").append("\n");
            gettingThere.append("<p />The nearest airport is East Midlands (code EMA) which is served by budget airlines BMI Baby, Easyjet and Ryanair. The airport has dedicated bus links to Derby and Nottingham, from where local services can be taken. Alternatively, you can fly to London airports and travel by train to Derby, Nottingham or Alfreton.").append("\n");
            gettingThere.append("<h4>Local Transport</h4>").append("\n");
            gettingThere.append("<h3>From Derby train station</h3>").append("\n");
            gettingThere.append("<p />Bus services 40, 41, 44 and 45 link Derby train and bus stations every 7 minutes, Monday to Saturday. Bus services 9.1 and 9.2 link Derby bus station to Midland Railway Butterley every 30 minutes, Monday to Saturday.").append("\n");
            gettingThere.append("<p />The H1 bus service also links Derby train station to the Golden Valley campsite.").append("\n");
            gettingThere.append("<h3>From Nottingham train station</h3>").append("\n");
            gettingThere.append("<p /Trent Barton Rainbow 1 goes from Nottingham to Ripley town centre, which is a 15 minute walk or a quick bus ride (9.1, 9.2 or 9.3) to Butterley station.<p />Alternatively (but slightly longer), you can catch the Red Arrow or Citylink bus to Derby, and follow the Derby journey above.").append("\n");
            gettingThere.append("<h3>From Alfreton train station</h3>").append("\n");
            gettingThere.append("<p />Bus services 9.1, 9.2 and 9.3 link Alfreton station to Midland Railway Butterley every 15 minutes, Monday to Saturday.").append("\n");
            gettingThere.append("<p />The H1 bus service also links Alfreton train station to the Golden Valley campsite.").append("\n");
            gettingThere.append("<h3>Taxi services</h3>").append("\n");
            gettingThere.append("<p />Alternatively, you could take a taxi from any of the train stations. For sat navs, ask your driver to enter the postcode DE5 3QZ. We can also recommend the following taxi companies:").append("\n");
            gettingThere.append("<h4>Amber Taxis (Alfreton)</h4>").append("\n");
            gettingThere.append("<p />+44 1773 836100").append("\n");
            gettingThere.append("<h4>Get Me To The Event (Ripley)</h4>").append("\n");
            gettingThere.append("<p />+44 7875 468504").append("\n");
            gettingThere.append("<h4>Trent Cars (Nottingham)</h4>").append("\n");
            gettingThere.append("<p />+44 115 950 5050").append("\n");
            gettingThere.append("<h4>Eagle City Cars (Derby)</h4>").append("\n");
            gettingThere.append("<p />+44 1332 200100").append("\n");
            gettingThere.append("<h3>Mini bus hire</h3>").append("\n");
            gettingThere.append("<p />Many people stay in surrounding towns like Derby and Nottingham and share a mini bus fare to the Midland Railway Centre. Journey time is about 30 minutes, and pre-booking is essential. We can recommend the following mini bus hire companies:").append("\n");
            gettingThere.append("<p />Gus the Bus 0800 678 5067").append("\n");
            gettingThere.append("<p />www.gusthebus.co.uk").append("\n");
            gettingThere.append("<p />Derby Mini Coaches 01332 757680").append("\n");
            gettingThere.append("<p />www.derbyminicoaches.com").append("\n");
            gettingThere.append("<h2>Car sharing</h2>").append("\n");
            gettingThere.append("<p />Car sharing will allow you to find people to travel with, while sharing the cost of the journey and hopefully making a difference to the environment. Simply visit www.gocarshare.com and log on via Facebook. If you are planning to drive, just post a car share request to the Indietracks Festival. In the message box you can outline any specifics such as the contribution you need for petrol, then wait for someone to contact you about sharing a lift. If you are a passenger wanting a lift, just type in Indietracks Festival and see which drivers are going there.").append("\n");
        }
        if (taxis.length() == 0) {
            taxis.append("<h2>Taxi numbers</h2>").append("\n");
            taxis.append("<h4>Amber Taxis (Alfreton)</h4>").append("\n");
            taxis.append("<p />+44 1773 836100").append("\n");
            taxis.append("<h4>Get Me To The Event (Ripley)</h4>").append("\n");
            taxis.append("<p />+44 7875 468504").append("\n");
            taxis.append("<h4>Trent Cars (Nottingham)</h4>").append("\n");
            taxis.append("<p />+44 115 950 5050").append("\n");
            taxis.append("<h4>Eagle City Cars (Derby)</h4>").append("\n");
            taxis.append("<p />+44 1332 200100").append("\n");
            taxis.append("<p />For sat navs, ask your driver to enter the postcode DE5 3QZ").append("\n");
        }
        if (about.length() == 0) {
            about.append("<h2>What is Indietracks?</h2>").append("\n");
            about.append("<p />Indietracks is a unique summer festival which combines heritage trains and indiepop music, and is located in the Derbyshire countryside. Guests are free to enjoy the regular facilities of the Midland Railway Butterley such as the steam train rides and museum, and enjoy a range of new and established indiepop bands.").append("\n");
            about.append("<p />The Indietracks Festival is a fundraiser which raises money for the Midland Railway Trust. Follow the links to find out a bit of history about the charity and the railway!").append("\n");
            about.append("<p />indietracks website").append("\n");
            about.append("<p />http://www.indietracks.co.uk").append("\n");
            about.append("<p />Midland Railway Centre Website").append("\n");
            about.append("<p />http://www.midlandrailwaycentre.co.uk").append("\n");
        }
        infoTextMap.put(GETTING_THERE, Html.fromHtml(gettingThere.toString()));
        infoTextMap.put(TAXIS, Html.fromHtml(taxis.toString()));
        infoTextMap.put(ABOUT, Html.fromHtml(about.toString()));
    }

    String infoType;
    View infoView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        //restore saved state if required
        if (savedInstanceState != null && savedInstanceState.containsKey(INFO_TYPE)) {
            infoType = savedInstanceState.getString(INFO_TYPE);
        }
        populateView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        if (infoType != null) {
            outState.putString(INFO_TYPE, infoType);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        infoView = inflater.inflate(R.layout.info_fragment, container, false);
        return infoView;
    }

    void populateView() {
        Bundle message = getArguments();
        if (message != null && message.containsKey(INFO_TYPE)) {
            infoType = message.getString(INFO_TYPE);
        }

        TextView header = (TextView) infoView.findViewById(R.id.info_header);
        header.setText(infoType);
        TextView content = (TextView) infoView.findViewById(R.id.info_text);
        content.setText(infoTextMap.get(infoType));
        content.setMovementMethod(LinkMovementMethod.getInstance());
        Linkify.addLinks(content, Linkify.ALL);

    }
}
