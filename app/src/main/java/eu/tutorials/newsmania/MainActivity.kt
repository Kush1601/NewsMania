package eu.tutorials.newsmania

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import nv.projects.newsapp.utils.StackLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.FieldPosition
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


class MainActivity : AppCompatActivity() {
    lateinit var adapter: NewsAdapter
    private var articles = mutableListOf<Article>()
    var pageNum = 1
    var totalResults = -1
    private var mInterstitialAd: InterstitialAd? = null
    private var TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Declaration adMob
        adGenerator()

        adapter = NewsAdapter(this@MainActivity,articles)
        val newsList = findViewById<RecyclerView>(R.id.newsList)
        newsList.adapter = adapter
        // newsList.layoutManager = LinearLayoutManager(this@MainActivity)

        val layoutManager = StackLayoutManager(StackLayoutManager.ScrollOrientation.BOTTOM_TO_TOP)
        layoutManager.setPagerMode(true)
        layoutManager.setItemChangedListener(object : StackLayoutManager.ItemChangedListener{
            override fun onItemChanged(position: Int){
            Log.d(TAG, "First Visible Item - ${layoutManager.getFirstVisibleItemPosition()}")
                Log.d(TAG, "Total Count - ${layoutManager.itemCount}")
                if(totalResults > layoutManager.itemCount && layoutManager.getFirstVisibleItemPosition() >= layoutManager.itemCount - 5){
                    // next page
                    pageNum ++
                    getNews()
                }
                if (position % 3 == 0) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd?.show(this@MainActivity)
                    } else {
                        Log.d("TAG", "The interstitial ad wasn't ready yet.")
                    }
                    //Repeated Ad Loop
                    adGenerator()
                }
            }
        })
        newsList.layoutManager = layoutManager
        getNews()
    }

    private fun adGenerator(){
        // Note - This method can also be called "adMob" instead of "adGenerator"
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                adError.toString().let { Log.d(TAG, it) }
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })
    }

    private fun getNews() {
        Log.d(TAG , "Request sent for $pageNum")
        val news = NewsService.newsInstance.getHeadlines("in",pageNum)
        news.enqueue(object : Callback<News> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<News>, response: Response<News>) {
                val newsData = response.body()
                if (newsData != null) {
                Log.d("NEWSMANIA", newsData.toString())
                    totalResults = newsData.totalResults
                    articles.addAll(newsData.articles)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                Log.d("NEWSMANIA", "Can't reach at the moment", t)
            }
        })
    }
}



