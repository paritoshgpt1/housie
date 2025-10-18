$(function () {
    const THEME_KEY = 'housie_theme';
    const VOICE_KEY = 'housie_voice';
    const ALLOWED_VOICES_URL = '/assets/voices/allowed.json';

    let bingo = {
        timeInterval: 7, // in seconds
        selectedNumbers: [],
        timer: null,
        isTimerOn: false,
        lastFive: [],
        sentences: {}, // active theme sentences map
        defaultSentences: {}, // Default theme fallback
        currentTheme: 'Default',
        currentVoice: 'Hindi Female',
        allowedVoiceNames: [], // enforced whitelist from allowed.json
        roundNumber: $("#roundNumber").text(),
        generateRandom: function () {
            const min = 1;
            const max = 90;
            return Math.floor(Math.random() * (max - min + 1)) + min;
        },
        addToLastFive: function (num) {
            if (bingo.lastFive.length === 5) {
                bingo.lastFive.pop();
            }
            bingo.lastFive.unshift(num);
            $("#lastFive").text(bingo.lastFive.join(", "));
        },
        generateNextRandom: function () {
            if (bingo.selectedNumbers.length === 90) {
                console.log(bingo.timer);
                clearInterval(bingo.timer);
                alert("All numbers Exhausted");
                return 0;
            }
            let random = bingo.generateRandom();
            while ($.inArray(random, bingo.selectedNumbers) > -1) {
                random = bingo.generateRandom();
            }
            bingo.selectedNumbers.push(random);
            bingo.addToLastFive(random);
            return random;
        },
        getSentence: function (numStr) {
            if (bingo.sentences && bingo.sentences[numStr]) return bingo.sentences[numStr];
            if (bingo.defaultSentences && bingo.defaultSentences[numStr]) return bingo.defaultSentences[numStr];
            return '';
        },
        setTimerOn: function () {
            $('#start').prop('disabled', true);
            $('#pause').prop('disabled', false);
            bingo.isTimerOn = true;
        },
        setTimerOff: function () {
            clearInterval(bingo.timer);
            $('#start').prop('disabled', false);
            $('#pause').prop('disabled', true);
            bingo.isTimerOn = false;
            console.log("timer off");
        }
    };

    function loadAllowedVoices() {
        return $.getJSON(ALLOWED_VOICES_URL)
            .then(function (data) {
                const arr = (data && Array.isArray(data.allowed)) ? data.allowed : [];
                // Fallback to at least Hindi Female if none configured
                bingo.allowedVoiceNames = arr.length ? arr : ['Hindi Female'];
            })
            .catch(function () {
                bingo.allowedVoiceNames = ['Hindi Female'];
            });
    }

    function populateBoardNumbers() {
        $('td').each(function () {
            let concatClass = this.cellIndex + "" + this.parentNode.rowIndex;
            let numberString = (parseInt(concatClass, 10) + 1).toString();
            $(this).addClass("cell" + numberString).text(numberString);
        });
    }

    function persistSelections() {
        try {
            localStorage.setItem(THEME_KEY, bingo.currentTheme);
            localStorage.setItem(VOICE_KEY, bingo.currentVoice);
        } catch (e) {
            // ignore storage errors
        }
    }

    function restoreSelections() {
        try {
            const savedTheme = localStorage.getItem(THEME_KEY);
            const savedVoice = localStorage.getItem(VOICE_KEY);
            if (savedTheme) bingo.currentTheme = savedTheme;
            if (savedVoice) bingo.currentVoice = savedVoice;
        } catch (e) {
            // ignore
        }
        $('#themeSelect').val(bingo.currentTheme);
        $('#voiceSelect').val(bingo.currentVoice);
    }

    function loadDefaultTheme() {
        return $.getJSON('/assets/themes/Default.json')
            .then(function (data) {
                bingo.defaultSentences = data || {};
            })
            .catch(function () {
                bingo.defaultSentences = {};
            });
    }

    function loadTheme(themeName) {
        bingo.currentTheme = themeName || 'Default';
        if (bingo.currentTheme === 'Default') {
            bingo.sentences = bingo.defaultSentences;
            persistSelections();
            return $.Deferred().resolve().promise();
        }
        return $.getJSON('/assets/themes/' + encodeURIComponent(bingo.currentTheme) + '.json')
            .then(function (data) {
                bingo.sentences = data || {};
                persistSelections();
            })
            .catch(function () {
                bingo.sentences = bingo.defaultSentences;
                bingo.currentTheme = 'Default';
                $('#themeSelect').val('Default');
                persistSelections();
            });
    }

    function populateVoicesWithResponsiveVoice() {
        if (typeof responsiveVoice === 'undefined' || !responsiveVoice || !responsiveVoice.voiceSupport()) {
            return false;
        }
        const voices = responsiveVoice.getVoices ? responsiveVoice.getVoices() : [];
        if (!voices || !voices.length) return false;
        // Filter voices against whitelist
        const voiceMap = new Map(voices.map(v => [v.name, v]));
        const filtered = bingo.allowedVoiceNames
            .map(name => voiceMap.get(name))
            .filter(Boolean);

        const $voice = $('#voiceSelect');
        $voice.empty();
        filtered.forEach(function (v) {
            $('<option>').val(v.name).text(v.name).appendTo($voice);
        });

        // Enforce allowed persisted voice; fallback to first allowed
        if ($voice.find('option[value="' + bingo.currentVoice + '"]').length) {
            $voice.val(bingo.currentVoice);
        } else {
            const firstAllowed = filtered.length ? filtered[0].name : null;
            if (firstAllowed) {
                bingo.currentVoice = firstAllowed;
                $voice.val(firstAllowed);
            } else {
                // No allowed voices available on this client; keep dropdown empty
                bingo.currentVoice = '';
            }
        }
        persistSelections();
        return true;
    }

    function initVoiceDropdown() {
        if (populateVoicesWithResponsiveVoice()) return;
        let attempts = 0;
        const maxAttempts = 10;
        const iv = setInterval(function () {
            attempts++;
            if (populateVoicesWithResponsiveVoice() || attempts >= maxAttempts) {
                clearInterval(iv);
            }
        }, 500);
    }

    // Initialize board and selections
    populateBoardNumbers();
    restoreSelections();
    // Load allowed voices config, then populate voices
    $.when(loadAllowedVoices()).always(function () {
        initVoiceDropdown();
    });

    // Load Default then selected theme with fallback
    $.when(loadDefaultTheme()).then(function () {
        loadTheme(bingo.currentTheme);
    });

    // Handlers for theme and voice selections
    $('#themeSelect').on('change', function () {
        const theme = $(this).val();
        loadTheme(theme);
    });
    $('#voiceSelect').on('change', function () {
        bingo.currentVoice = $(this).val();
        persistSelections();
    });

    $('#btnGenerate').click(function () {
        let random = bingo.generateNextRandom().toString();
        $('#number').text(random);
        $('td.cell' + random).addClass('selected');
        let numberLine = bingo.getSentence(random);
        $('#numberLine').text(numberLine);
        if (typeof responsiveVoice !== 'undefined' && responsiveVoice) {
            // Speak only if current voice is allowed and present
            const canSpeak = bingo.currentVoice && (
                !bingo.allowedVoiceNames.length || bingo.allowedVoiceNames.indexOf(bingo.currentVoice) !== -1
            );
            if (canSpeak) {
                responsiveVoice.speak(numberLine + " is " + random, bingo.currentVoice);
            } else {
                console.warn('No allowed voice available to speak.');
            }
        }
        markNumberOnBoard(random, bingo.roundNumber);
        $("#claimNumber").val(random);
    });
    $('#start').click(function () {
        bingo.setTimerOn();
        $('#btnGenerate').click();
        bingo.timer = setInterval(function () {
            $('#btnGenerate').click();
        }, bingo.timeInterval * 1000);
    });
    $('#pause').click(function () {
        bingo.setTimerOff();
    });
    $(document).keypress(function (e) {
        if (e.key === ' ' || e.key === 'Spacebar') {
            e.preventDefault();
            if (bingo.isTimerOn) {
                bingo.setTimerOff();
            } else {
                $('#start').click();
            }
        }
    });
    $("#validate").click(function () {
        console.log("inside validate");
        let ticketNumber = $("#claimTicketNumber").val();
        getTicketDetails(ticketNumber, bingo.roundNumber);
    });
    window.onbeforeunload = function (e) {
        let returnString;
        returnString = 'Are you sure?';
        if (e) {
            e.returnValue = returnString;
        }
        return returnString;
    };
    $("#updateSpeed").click(function () {
        bingo.setTimerOff();
        bingo.timeInterval = $("#speed").val();
        console.log(bingo.timeInterval);
    });
});

function markNumberOnBoard(number, roundNumber) {
    $.post(
        "/test",
        {
            number: number,
            roundNumber: roundNumber
        },
        function () {
            console.log("Number Saved in DB");
        }
    );
}

function getTicketDetails(ticketNumber, roundNumber) {
    console.log("ticketNumber: " + ticketNumber);
    $.get(
        "/ticket-details",
        {
            ticketNumber: ticketNumber,
            roundNumber: roundNumber
        },
        function (data) {
            console.log(data);
            validateClaim(data, ticketNumber);
        }
    );
}

function validateClaim(data, ticketNumber) {
    data.roundNumber = $("#roundNumber").text();
    data.currentNumber = parseInt($("#claimNumber").val());
    let claim = $("#claim").val();
    let validate = new Validate(data);
    validate.checkDividends(claim, ticketNumber);
}
