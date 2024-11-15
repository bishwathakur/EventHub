import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ProgressBar
import androidx.compose.ui.res.integerArrayResource
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.eventhub.AddEventActivity
import com.example.eventhub.NewMessageActivity
import com.example.eventhub.PostDetailsActivity
import com.example.eventhub.R
import com.example.eventhub.ShareActivity
import com.example.eventhub.adapter.PostAdapter
import com.example.eventhub.models.Post
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.ArrayList

class Home : Fragment() {

    private lateinit var eveRecyclerView: RecyclerView
    private lateinit var loadingcircle: ProgressBar
    private lateinit var addBtn: FloatingActionButton
    private lateinit var eveList: ArrayList<Post>
    private lateinit var evedetRef: DatabaseReference
    private lateinit var eveRef: DatabaseReference
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mAdapter: PostAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        eveRecyclerView = view.findViewById(R.id.home_rec)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        eveRecyclerView.layoutManager = LinearLayoutManager(context)
        eveRecyclerView.setHasFixedSize(true)
        loadingcircle = view.findViewById(R.id.home_progress_bar)

        eveList = arrayListOf<Post>()

        // Initialize databaseReference and firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        evedetRef = FirebaseDatabase.getInstance().getReference("EventDetails")
        eveRef = FirebaseDatabase.getInstance().getReference("Events")



        mAdapter = PostAdapter(eveList, firebaseAuth, evedetRef, eveRef, isProfileFragment = false)
        
        eveRecyclerView.adapter = mAdapter

        mAdapter.onItemClick = {
            val intent = Intent(activity, PostDetailsActivity::class.java)
            intent.putExtra("post", it)
            startActivity(intent)
        }
        mAdapter.onShareClick = { post ->

            // Create the deep link to the specific post
            val deepLink = "eventhub://post/details/${post.eventKey}"

                // Format the content for sharing
                val shareText = """
            Check out this event!
            Event Name: ${post.eventname}
            Date: ${post.eventdate}
            Venue: ${post.eventvenue}
            Likes: ${post.postLikes}
            Registrations: ${post.postRegistrations}
            Comments: ${post.postComments}
            Organized by: ${post.userName}
            
            Click here to view the event: 
             $deepLink
            """.trimIndent()

                // Create the sharing intent
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }

                // Show the share sheet (chooser)
                val chooser = Intent.createChooser(shareIntent, "Share Event via")
                startActivity(chooser)
            }






        // Initialize addBtn and set its click listener
        addBtn = view.findViewById(R.id.home_addButton)
        addBtn.setOnClickListener {

            val intent = Intent(activity, AddEventActivity::class.java)
            startActivity(intent)
        }

        getEvents()

        swipeRefreshLayout.setOnRefreshListener {

            reloadAdapter()

            swipeRefreshLayout.isRefreshing = false
        }

        return view
    }

    private fun getEvents() {
        eveRecyclerView.visibility = View.GONE
        loadingcircle.visibility = View.VISIBLE

        eveRef = FirebaseDatabase.getInstance().getReference("Events")

        eveRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eveList.clear()
                if (snapshot.exists()) {
                    for (eveSnap in snapshot.children) {
                        val eveData = eveSnap.getValue(Post::class.java)
                        eveList.add(eveData!!)
                    }
                    // Notify the adapter that the dataset has changed
                    mAdapter.notifyDataSetChanged()
                }



                eveRecyclerView.visibility = View.VISIBLE
                loadingcircle.visibility = View.GONE


            }

            override fun onCancelled(error: DatabaseError) {
                loadingcircle.visibility = View.GONE
            }
        })
    }

    private fun reloadAdapter() {
        getEvents()
        // Notify the adapter that the dataset has changed
        mAdapter.notifyDataSetChanged()
    }
}
