$(function() {
    var bingo = {
        timeInterval: 7, // in seconds
        selectedNumbers: [],
        timer: null,
        isTimerOn: false,
        lastFive: [],
        bingoNumberWords: ["Top of the house number 1","Kaala dhan","Goodness Me","Knock at the door","Symbol of congress","Super sixer","Colours of rainbow","Big fat lady number 8","Number of planets in solar system number 9","A big fat hen","Amitabh's legs","One dozen","Unlucky for some lucky for me no. thirteen","Valentine Day","Yet to be kissed","Sweet sixteen","Dancing Queen","Voting age","End of the teens","Blind 20","President salute","Two little ducks","You and me","Two dozen","Silver Jublee Number","Republic Day","Gateway to heaven","Duck and its mate","In your prime","Its middle Age","Time for fun","Mouth Full","All the 3s","Dil mange more","Flirty Husband","Popular Number","Mixed luck","Oversize","Watch your waistline","Naughty 40","Life's begun at 41","Quit India Movement","Pain in the knee","All the fours","Halfway there","Up to tricks","Year of Independence","Four dozen","Rise and shine","Half a century, Golden Jublee","Charity begins at 51","Pack of cards","Pack with a joker","Pack with two jokers","All the fives","Pick up sticks","Mutiny Year","Time to retire","Just retired","Five dozen","Bakers bun","Click the two","Click the three","Catch the chor","Old age pension","Chakke pe chakka","Made in heaven","Saving grace","Ulta Pulta","Lucky blind","Lucky bachelor","Lucky couple","A crutch and a flea","Lucky chor","Diamond Jublee","Lucky six","Two hockey sticks","Heaven's gate","lucky nine","Gandhi's breakfast","Corner shot","Last of the two","India wins Cricket World Cup","Last of the chors","Grandma","Last six","Grandpa","Two fat ladies","All but one","Top of the house"],
        roundNumber: $("#roundNumber").text(),
        generateRandom: function() {
            var min = 1;
            var max = 90;
            var random = Math.floor(Math.random() * (max - min + 1)) + min;
            return random;
        },
        addToLastFive: function(num) {
            if (bingo.lastFive.length == 5) {
                bingo.lastFive.pop();
            }
            bingo.lastFive.unshift(num);
            $("#lastFive").text(bingo.lastFive.join(", "));
        },
        generateNextRandom: function() {
            if (bingo.selectedNumbers.length == 90) {
                console.log(bingo.timer);
                clearInterval(bingo.timer);
                alert("All numbers Exhausted");
                return 0;
            }
            var random = bingo.generateRandom();
            while ($.inArray(random, bingo.selectedNumbers) > -1) {
                random = bingo.generateRandom();
            }
            bingo.selectedNumbers.push(random);
            bingo.addToLastFive(random);
            return random;
        },
        say: function(m) {
          var msg = new SpeechSynthesisUtterance();
          var voices = window.speechSynthesis.getVoices();
          msg.voice = voices[39];
          msg.voiceURI = "Veena";
          msg.volume = 1;
          msg.rate = 0.8;
          msg.pitch = 0.8;
          msg.text = m;
          msg.lang = 'en-IN';
          speechSynthesis.speak(msg);
        },
        setTimerOn: function() {
             $('#start').prop('disabled', true);
             $('#pause').prop('disabled', false);
             bingo.isTimerOn = true;
        },
        setTimerOff: function() {
             clearInterval(bingo.timer);
             $('#start').prop('disabled', false);
             $('#pause').prop('disabled', true);
             bingo.isTimerOn = false;
        }
    };
    $('td').each(function() {
        var concatClass = this.cellIndex + "" + this.parentNode.rowIndex;
        var numberString = (parseInt(concatClass, 10) + 1).toString();
        $(this).addClass("cell" + numberString).text(numberString);
    });
    $('#btnGenerate').click(function() {
        var random = bingo.generateNextRandom().toString();
        $('#number').text(random);
        $('td.cell' + random).addClass('selected');
        var numberLine = bingo.bingoNumberWords[parseInt(random)-1];
        $('#numberLine').text(numberLine);
        // bingo.say(numberLine + " is " + random);
        responsiveVoice.speak(numberLine + " is " + random, "Hindi Female");
        markNumberOnBoard(random, bingo.roundNumber);
        $("#claimNumber").val(random);
    });
    $('#start').click(function() {
        bingo.setTimerOn();
        $('#btnGenerate').click();
        bingo.timer = setInterval(function() {
            $('#btnGenerate').click();
        }, bingo.timeInterval * 1000);
    });
    $(document).keypress(function(e) {
        if (e.key === ' ' || e.key === 'Spacebar') {
            e.preventDefault();
            if (bingo.isTimerOn) {
                bingo.setTimerOff();
            } else {
                bingo.setTimerOn();
                $('#start').click();
            }
        }
    });
    $("#validate").click(function () {
        console.log("inside validate");
        var ticketNumnber = $("#claimTicketNumber").val();
        getTicketDetails(ticketNumnber, bingo.roundNumber);
    });
    window.onbeforeunload = function(e) {
        e = e || window.event;
        var returnString = 'Are you sure?';
        if (e) {
            e.returnValue = returnString;
        }
        return returnString;
    };
});
function markNumberOnBoard(number, roundNumber) {
    $.post(
        "/test",
        {
            number: number,
            roundNumber: roundNumber
        },
        function() {
            console.log("Number Saved in DB");
        }
    );
}
function getTicketDetails (ticketNumber, roundNumber) {
    console.log("ticketNumber: " + ticketNumber);
    $.get(
        "/ticket-details",
        {
            ticketNumber: ticketNumber,
            roundNumber: roundNumber
        },
        function(data) {
            console.log(data);
            validateClaim(data, ticketNumber);
        }
    );
}
function validateClaim(data, ticketNumber) {
    data.roundNumber = $("#roundNumber").text();
    data.currentNumber = parseInt($("#claimNumber").val());
    var claim = $("#claim").val();
    validate = new Validate(data);
    validate.checkDividends(claim, ticketNumber);
}