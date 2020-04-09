class Validate {
    constructor(data) {
        // param ticketNumbers: 2D array of 3 rows and 5 columns
        // param allTicketsNumbers: 2D array of 3 rows and 9 columns
        // param roundNumbers: array of all numbers in the ticket which have been called already
        // param currentNumber: the current number in the ticket which has been called
        this.ticketNumbers = data.ticketNumbers;
        this.allTicketNumbers = data.allTicketNumbers;
        this.roundNumbers = new Set(data.roundNumbers);
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
                break;
            case "4corners":
                this.checkfor4Corners();
                break;
            case "bamboo":
                this.checkforBamboo();
                break;
            case "l":
                this.checkforL();
                break;
            case "t":
                this.checkforT();
                break;
            case "h":
                this.checkforH();
                break;
            case "topline":
                this.checkforTopLine();
                break;
            case "middleline":
                this.checkforMiddleLine();
                break;
            case "bottomline":
                this.checkforBotttomLine();
                break;
            case "breakfast":
                this.checkforBreakfast();
                break;
            case "lunch":
                this.checkforLunch();
                break;
            case "dinner":
                this.checkforDinner();
                break;
            case "zona":
                this.checkforZona();
                break;
            case "temp":
                this.checkforTemperature();
                break;
            case "pyramid":
                this.checkforPyramid();
                break;
            case "raindrop":
                this.checkforRaindrop();
                break;
            case "younger":
                this.checkforYounger();
                break;
            case "older":
                this.checkforOlder();
                break;
            case "fullhouse":
                this.checkforFullHouse();
                break;
            default:
                alert("Invalid Claim");
        }
    }

    // function checkForEarly7: checks if the player has hit the Early7 dividend
    checkForEarly7() {
        if (this.checkNumbersMarked(this.ticketNumbers.flat(), 7)) {
            alert("Early 7 correctly claimed");
        } else {
            this.boogie();
        }
    }

    checkfor4Corners() {
        let numbersToCheck = [];
        numbersToCheck.push(this.ticketNumbers[0][0]);
        numbersToCheck.push(this.ticketNumbers[0][4]);
        numbersToCheck.push(this.ticketNumbers[2][0]);
        numbersToCheck.push(this.ticketNumbers[2][4]);
        if (this.checkNumbersMarked(numbersToCheck, 4)) {
            alert("4 Corners correctly claimed");
        } else {
            this.boogie();
        }
    }

    checkforBamboo() {
        let numbersToCheck = [];
        numbersToCheck.push(this.ticketNumbers[0][2]);
        numbersToCheck.push(this.ticketNumbers[1][2]);
        numbersToCheck.push(this.ticketNumbers[2][2]);
        if (this.checkNumbersMarked(numbersToCheck, 3)) {
            alert("Bamboo correctly claimed");
        } else {
            this.boogie();
        }
    }

    checkforL() {
        let numbersToCheck = [];
        numbersToCheck.push(this.ticketNumbers[0][0]);
        numbersToCheck.push(this.ticketNumbers[1][0]);
        numbersToCheck.concat(this.ticketNumbers[2]);
        if (this.checkNumbersMarked(numbersToCheck, 7)) {
            alert("L correctly claimed");
        } else {
            this.boogie();
        }
    }

    checkforT() {
        let numbersToCheck = [];
        numbersToCheck.concat(this.ticketNumbers[0]);
        numbersToCheck.push(this.ticketNumbers[1][2]);
        numbersToCheck.push(this.ticketNumbers[2][2]);
        if (this.checkNumbersMarked(numbersToCheck, 7)) {
            alert("T correctly claimed");
        } else {
            this.boogie();
        }
    }

    checkforH() {
        let numbersToCheck = [];
        numbersToCheck.push(this.ticketNumbers[0][0]);
        numbersToCheck.push(this.ticketNumbers[2][0]);
        numbersToCheck.concat(this.ticketNumbers[1]);
        numbersToCheck.push(this.ticketNumbers[0][4]);
        numbersToCheck.push(this.ticketNumbers[2][4]);
        if (this.checkNumbersMarked(numbersToCheck, 9)) {
            alert("H correctly claimed");
        } else {
            this.boogie();
        }
    }

    checkforTopLine() {
        if (this.checkNumbersMarked(this.ticketNumbers[0], 5)) {
            alert("Top Line correctly claimed");
        } else {
            this.boogie();
        }
    }

    checkforMiddleLine() {
        if (this.checkNumbersMarked(this.ticketNumbers[1], 5)) {
            alert("Middle Line correctly claimed");
        } else {
            this.boogie();
        }
    }

    checkforBotttomLine() {
        if (this.checkNumbersMarked(this.ticketNumbers[2], 5)) {
            alert("Bottom Line correctly claimed");
        } else {
            this.boogie();
        }
    }

    checkforZona() {
        let numbersToCheck = this.ticketNumbers.flat();
        for (let i = 0; i < numbersToCheck.length; i++) {
            if(this.roundNumbers.has(numbersToCheck[i])) {
                this.boogie();
                return;
            }
        }
        alert("Zona correctly claimed");
    }

    checkforTemperature() {
        let numbersToCheck = [];
        numbersToCheck.push(this.ticketNumbers[0][0]);
        numbersToCheck.push(this.ticketNumbers[2][4]);
        if (this.checkNumbersMarked(numbersToCheck, 2)) {
            alert("Temperature correctly claimed");
        } else {
            this.boogie();
        }
    }

    checkforPyramid() {
        let numbersToCheck = [];
        numbersToCheck.push(this.ticketNumbers[0][2]);
        numbersToCheck.push(this.ticketNumbers[1][1]);
        numbersToCheck.push(this.ticketNumbers[1][3]);
        numbersToCheck.push(this.ticketNumbers[2][0]);
        numbersToCheck.push(this.ticketNumbers[2][2]);
        numbersToCheck.push(this.ticketNumbers[2][4]);
        if (this.checkNumbersMarked(numbersToCheck, 6)) {
            alert("Pyramid correctly claimed");
        } else {
            this.boogie();
        }
    }

    checkforFullHouse() {
        if (this.checkNumbersMarked(this.ticketNumbers.flat(), 15)) {
            alert("Full House correctly claimed");
        } else {
            this.boogie();
        }
    }

    checkforBreakfast(){}
    checkforLunch(){}
    checkforDinner(){}
    checkforYounger(){}
    checkforOlder(){}
    checkforRaindrop(){}


    // function checkNumbersMarked: checks whether the numbers to check have been actually announced or not
    // numbersToCheck: list of numbers to be checked
    // count: the expected count of numbers to be marked
    checkNumbersMarked(numbersToCheck, count) {
        let counter = 0;
        let currentNumberPresent = false;
        for (let i = 0; i < numbersToCheck.length; i++) {
            if(this.roundNumbers.has(numbersToCheck[i])) {
                counter++;
                if (numbersToCheck[i] === this.currentNumber) {
                    currentNumberPresent = true;
                }
            }
        }
        return currentNumberPresent && counter === count;
    }

    boogie() {
        alert("Incorrect claim. Boogie!!!");
    }
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
