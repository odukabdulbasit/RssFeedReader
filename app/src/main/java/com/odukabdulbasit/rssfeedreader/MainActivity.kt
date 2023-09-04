package com.odukabdulbasit.rssfeedreader

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : AppCompatActivity() {

    private lateinit var fetchButton : Button
    private lateinit var titleTextView : TextView
    lateinit var descriptionTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchButton = findViewById(R.id.fetchButton)
        titleTextView = findViewById(R.id.titleTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)

        val rssFeedUrl = "https://example.com/rss-feed.xml" // Replace with your RSS feed URL

        fetchButton.setOnClickListener {
            fetchAndDisplayRssFeed(rssFeedUrl)
        }
    }

    private fun fetchAndDisplayRssFeed(url: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val rssFeedItems = fetchRssFeed(url)

                if (rssFeedItems != null && rssFeedItems.length > 0) {
                    val item = rssFeedItems.item(0) as Element // Assuming you want to display the first item

                    val title = item.getElementsByTagName("title").item(0).textContent
                    val description = item.getElementsByTagName("description").item(0).textContent

                    // Update the UI on the main thread
                    withContext(Dispatchers.Main) {
                        titleTextView.text = "Title: $title"
                        descriptionTextView.text = "Description: $description"
                        // Update other TextViews as needed
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun fetchRssFeed(url: String): NodeList? {
        val client = HttpClient()

        val response: HttpResponse = client.get(url) {
            accept(ContentType.Application.Xml)
        }

        val xmlContent = response.readText()

        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()
        val document = documentBuilder.parse(xmlContent.byteInputStream())

        return document.documentElement.getElementsByTagName("item")
    }
}

