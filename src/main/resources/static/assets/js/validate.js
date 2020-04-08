class Validate {
    constructor(data) {
        // param ticketNumbers: 2D array of 3 rows and 5 columns
        // param allTicketsNumbers: 2D array of 3 rows and 9 columns
        // param roundNumbers: array of all numbers in the ticket which have been called already
        // param currentNumber: the current number in the ticket which has been called
        this.ticketNumbers = data.ticketNumbers;
        this.allTicketNumbers = data.allTicketNumbers;
        this.roundNumbers = data.roundNumbers;
        this.currentNumber = data.currentNumber;
        this.numberOfRows = 3;
        this.columnsWithZeros = 9;
        this.columnsWithoutZeros = 5;
    }

    // function checkDividends: checks if the player has hit any dividend
    checkDividends(claim) {
        switch (claim) {
            case "early7":
                this.checkForEarly7();
        }
    }

    // function checkForEarly7: checks if the player has hit the Early7 dividend
    checkForEarly7() {
        var counter = 0;
        var currentNumberPresent = false;
        for (var i = 0; i < this.numberOfRows; i++) {
            for (var j = 0; j < this.columnsWithoutZeros; j++) {
                if (this.roundNumbers.indexOf(this.ticketNumbers[i][j]) !== -1) {
                    counter++;
                    if (this.ticketNumbers[i][j] === this.currentNumber) {
                        currentNumberPresent = true;
                    }
                }
            }
        }
        if (currentNumberPresent && counter === 7) {
            alert("Early 7 correctly claimed");
        }
    }
}

// function checkForEarly7: checks if the player has hit the Early7 dividend
// param ticket: 2D array of 3 rows and 9 columns
// param markedNumbers: array of all numbers in the ticket which have been called already
// param currentNumber: the current number in the ticket which has been called
function

checkForEarly7(ticket, markedNumbers, currentNumber) {
    if (markedNumbers.indexOf(currentNumber) !== -1 && markedNumbers.length == 7) {
        return "Early 7"
    }
    return "";
}

// function checkForCorners: checks if the player has hit the Corner dividend
// param ticket: 2D array of 3 rows and 9 columns
// param markedNumbers: array of all numbers in the ticket which have been called already
// param currentNumber: the current number in the ticket which has been called
function checkForCorners(ticket, markedNumbers, currentNumber) {
    if (markedNumbers.indexOf(currentNumber) !== -1 && markedNumbers.length >= 4) {
        var topLeft = false;
        var topRight = false;
        var bottomLeft = false;
        var bottomRight = false;

        var topLeftNumber = 0;
        var topRightNumber = 0;
        var bottomLeftNumber = 0;
        var bottomRightNumber = 0;

        // check for topLeft corner
        for (var i = 0; i < ticket[0].length; i++) {
            if (ticket[0][i] == 0) {
                continue;
            } else {
                if (markedNumbers.indexOf(ticket[0][i]) !== -1) {
                    topLeft = true;
                    topLeftNumber = ticket[0][i]
                }
                break;
            }
        }

        // check for topRight corner
        for (var i = ticket[0].length - 1; i >= 0; i--) {
            if (ticket[0][i] == 0) {
                continue;
            } else {
                if (markedNumbers.indexOf(ticket[0][i]) !== -1) {
                    topRight = true;
                    topRightNumber = ticket[0][i]
                }
                break;
            }
        }

        // check for bottomLeft corner
        for (var i = 0; i < ticket[2].length; i++) {
            if (ticket[2][i] == 0) {
                continue;
            } else {
                if (markedNumbers.indexOf(ticket[2][i]) !== -1) {
                    bottomLeft = true;
                    bottomLeftNumber = ticket[2][i]
                }
                break;
            }
        }

        // check for bottomRight corner
        for (var i = ticket[2].length - 1; i >= 0; i--) {
            if (ticket[2][i] == 0) {
                continue;
            } else {
                if (markedNumbers.indexOf(ticket[2][i]) !== -1) {
                    bottomRight = true;
                    bottomRightNumber = ticket[2][i]
                }
                break;
            }
        }

        // return the corener dividend if all corners have been marked
        if (topLeft && topRight && bottomLeft && bottomRight) {
            if (topLeftNumber == currentNumber || topRightNumber == currentNumber || bottomLeftNumber == currentNumber || bottomRightNumber == currentNumber) {
                return "Corners"
            }
            return "";
        }

    }
    return "";
}
