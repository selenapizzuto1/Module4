

package com.raywenderlich.placebook.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.raywenderlich.placebook.model.Bookmark
import com.raywenderlich.placebook.repository.BookmarkRepo

class MapsViewModel(application: Application) : AndroidViewModel(application) {

  private val TAG = "MapsViewModel"

  private var bookmarkRepo: BookmarkRepo = BookmarkRepo(
      getApplication())
  private var bookmarks: LiveData<List<BookmarkMarkerView>>?
      = null

  fun addBookmarkFromPlace(place: Place, image: Bitmap?) {

    val bookmark = bookmarkRepo.createBookmark()
    bookmark.placeId = place.id
    bookmark.name = place.name.toString()
    bookmark.longitude = place.latLng?.longitude ?: 0.0
    bookmark.latitude = place.latLng?.latitude ?: 0.0
    bookmark.phone = place.phoneNumber.toString()
    bookmark.address = place.address.toString()

    val newId = bookmarkRepo.addBookmark(bookmark)
    Log.i(TAG, "New bookmark $newId added to the database.")
  }

  fun getBookmarkMarkerViews() :
      LiveData<List<BookmarkMarkerView>>? {
    if (bookmarks == null) {
      mapBookmarksToMarkerView()
    }
    return bookmarks
  }

  private fun mapBookmarksToMarkerView() {
    bookmarks = Transformations.map(bookmarkRepo.allBookmarks) { repoBookmarks ->
      repoBookmarks.map { bookmark ->
        bookmarkToMarkerView(bookmark)
      }
    }
  }

  private fun bookmarkToMarkerView(bookmark: Bookmark): MapsViewModel.BookmarkMarkerView {
    return MapsViewModel.BookmarkMarkerView(
        bookmark.id,
        LatLng(bookmark.latitude, bookmark.longitude))
  }

  data class BookmarkMarkerView(var id: Long? = null,
                                var location: LatLng = LatLng(0.0, 0.0))
}