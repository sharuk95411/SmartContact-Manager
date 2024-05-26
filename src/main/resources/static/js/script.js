console.log("This is Script file")
// ye new way h function bnane ka jo us time JS ku latest update me aya h

var jQueryScript = document.createElement('script');
jQueryScript.setAttribute('src', 'https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js');
document.head.appendChild(jQueryScript);
const toggleSidebar = () => {
    if ($(".sidebar").is(":visible")) {
        // Yha show kr rha hga to click (call) krne k bad bnd ho jyga
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");
    }
    else {
        // agr close hga to click (Call krne) pr open ho jyga
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }

};


const search = () => {
    // console.log("Searching ...");

    let query = $("#search-input").val();

    if (query == "") {
        $(".search-result").hide();
    } else {
        console.log(query);


        // Sending Request to Server
         let url = `http://localhost:8282/search/${query}`;
        
        fetch(url)
            .then((response) => {
                return response.json();
            })
            .then((data) => {
                console.log(data);

                let text = `<div class='list-group'>`;
                data && data.forEach((contact) => {
                    text += `<a href='/user/${contact.cId}/contact' class='list-group-item list-group-item-action'> ${contact.name} </a>`
                });
                text += `</div>`;
                $(".search-result").html(text);
                $(".search-result").show();
            });


    }
};
