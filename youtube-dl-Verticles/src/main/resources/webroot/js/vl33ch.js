	$( "#videoid" ).keypress(function() {
		$( "form#additem" ).find("input[id='format']").val("");
	});
	
	
	function addAlert(message, classe='alert-info') {

		div = (
		'<div class="alert alert-warning alert-dismissible fade show" role="alert">'
		+message+
		'  <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>'+
		'</div>'
		);
		
		
		
		
		$("#alertbox").append(div);
	}


	function downloadVideo() {
		videoid = $( "form#additem" ).find("input[id='videoid']").val();
		videoformat = $( "form#additem" ).find("input[id='format']").val();
		
		if (videoid==null)
		{
			addAlert("No video id entered");
			return;
		}
		
		videoid = encodeURIComponent(videoid);
		
		if (!videoformat) 
		{
			addAlert("No videoformat id entered");
			return;
		}

		
		addAlert("Downloading ... with format "+videoformat);
		$('#button_download').prop('disabled', true);
		
		var jqxhr = $.ajax("/getvideo/v1/youtubedl/get/"+videoid+"/"+videoformat, {
			dataType : "text"
		}).done(function(data) {
			
			//addAlert("Popuping the video : "+data);
			
			var link = "/getvideo/v1/youtubedl/download/"+data;
			addAlert("<a href='"+link+"'>Download</a> the video");
			
			//window.open("/api/v1/youtubedl/download/"+data);
			

		}).fail(function(xhr, textStatus, errorThrown) {
			addAlert("Error fetching datas : "+xhr.responseText, "alert-danger");
			
			
		}).always(function() {
			//  alert( "complete" );
			$('#button_download').prop('disabled', false);
		});

		
		
		
		/*
		
		
		
		
		var url = "/api/v1/youtubedl/get/"+videoid;
		
		
		
		window.open(url);*/
	}
	
	function getFormats() {

		videoid = $( "form#additem" ).find("input[id='videoid']").val();
		
		$("input#format").val("");
		
		if (videoid==null)
		{
			addAlert("No video id entered");
			return;
		}

		videoid = encodeURIComponent(videoid);
		
		$("#formatsbox").text("Fetching file format ...");
		
		var tbody = $("#table_example").find("tbody").empty();

		$('#button_getformats').prop('disabled', true);
		
		var jqxhr = $.ajax("/getvideo/v1/youtubedl/format/"+videoid, {
			dataType : "json"
		}).done(function(data) {
			var jsonPretty = JSON.stringify(data, null, '\t');

			data = data.formats;

			JSONToTable(data);
			
			$("#formatsbox").html(data);

		}).fail(function() {
			addAlert("Error fetching datas", "alert-danger");
			$("#formatsbox").text("Error fetching datas :(");
			
		}).always(function() {
			//  alert( "complete" );
			$('#button_getformats').prop('disabled', false);
		});

	}

	function JSONToTable(data) {
		//var thead = $("#table_example").find("thead");
		var tbody = $("#table_example").find("tbody");

		//thead.empty();
		tbody.empty();
		
		if (data.length==0)
		{
			tbody.text("Database is empty!");
		}
	
		// Get table body and print
		for (var i = 0; i < Object.keys(data).length; i++) {
			var idformat = data[i][Object.keys(data[0])[0]];
			//tbody.append('<tr onclick="selectFormat('+idformat+');">');
			
			var tr = document.createElement("TR"); //  onclick='selectFormat(idformat);'
			tr.onclick = (function (idformat) {
				return function() { 
					selectFormat(idformat);
					};
				})(idformat);
			
			for (var j = 0; j < Object.keys(data[0]).length; j++) {
				var td = document.createElement("TD");
				td.appendChild(document.createTextNode(data[i][Object.keys(data[0])[j]]));
				//tbody.append('<td  onclick="selectFormat('+idformat+');">' + data[i][Object.keys(data[0])[j]]+ '</td>');
				tr.appendChild(td);
			}
			tbody.append(tr);
		}
	}
	
	function selectFormat(id)
	{
		$("input#format").val(id);
	}
		
	function deleteAllNotifications()
	{
		$("#alertbox").text("");
	}