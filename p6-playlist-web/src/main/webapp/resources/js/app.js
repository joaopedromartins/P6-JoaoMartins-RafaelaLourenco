$(function() {
	var account = $("#accountScreen");
	var playlist = $("#playlistScreen");
	var accountBtn = $("#accountNav");
	var playlistBtn = $("#playlistNav");
	
	playlistBtn.click(function() {
		playlistBtn.addClass("selectedLink");
		accountBtn.removeClass("selectLink");
		playlist.show();
		account.hide();
		$("li.selected").removeClass("selected");
		playlistBtn.parent().addClass("selected");
		$(".accountDetailsForm").hide();
		
		return false;
	});
	
	accountBtn.click(function() {
		accountBtn.addClass("selectedLink");
		playlistBtn.removeClass("selectLink");
		account.show();
		playlist.hide();
		$("li.selected").removeClass("selected");
		accountBtn.parent().addClass("selected");
		
		return false;
	});
	
	
	var audioPlayer = $("#audioPlayer")[0];
	
	$(".musicRow").click(function() {
		$(".musicEditRow").hide();
		var id = $(this).attr('id');
		$(".edit"+id).show();
	});
	
	
	$(".playBtn").click(function(e) {
		var music = $(this)[0].nextElementSibling.value;
		audioPlayer.src = music;
		audioPlayer.play();
		return false;
	});
	
	
	$("#usernameEditor").click(function(e) {
		$(".accountDetailsForm").hide();
		$("#usernameDetailsForm").show();
		return false;
	});
	
	$("#emailEditor").click(function(e) {
		$(".accountDetailsForm").hide();
		$("#emailDetailsForm").show();
		return false;
	});
	
	$("#passwordEditor").click(function(e) {
		$(".accountDetailsForm").hide();
		$("#passwordDetailsForm").show();
		return false;
	});
	
	$("#closeAccountEditor").click(function(e) {
		$(".accountDetailsForm").hide();
		$("#closeAccountForm").show();
		return false;
	});
	
	$("#newMusicBtn").click(function(e) {
		$("#musicUploaderFormContainer").toggle();
		return false;
	});
	
	$("#closeAccountBtn").click(function() {
		var response = confirm("Are you sure?");
		if (!response) return false;
	});
	
	$(".editMusicBtn").click(function() {
		var id = $(this).attr("id");
		var n = id.substring(12, id.length);
		var  row = $("#musicEditorRow"+n);
		
		if (!row.hasClass("hidden"))  {
			row.addClass("hidden");
			return false;
		}
		else {
			$(".userMusicRowEditor").addClass("hidden");
			row.removeClass("hidden");
		}
		
		return false;
	});
	
});