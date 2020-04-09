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
        if (this.checkAllNumbersMarked(numbersToCheck)) {
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
        if (this.checkAllNumbersMarked(numbersToCheck)) {
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
        if (this.checkAllNumbersMarked(numbersToCheck)) {
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
        if (this.checkAllNumbersMarked(numbersToCheck)) {
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
        if (this.checkAllNumbersMarked(numbersToCheck)) {
            alert("H correctly claimed");
        } else {
            this.boogie();
        }
    }

    checkforTopLine() {
        if (this.checkAllNumbersMarked(this.ticketNumbers[0])) {
            alert("Top Line correctly claimed");
        } else {
            this.boogie();
        }
    }

    checkforMiddleLine() {
        if (this.checkAllNumbersMarked(this.ticketNumbers[1])) {
            alert("Middle Line correctly claimed");
        } else {
            this.boogie();
        }
    }

    checkforBotttomLine() {
        if (this.checkAllNumbersMarked(this.ticketNumbers[2])) {
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
        if (this.checkAllNumbersMarked(numbersToCheck)) {
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
        if (this.checkAllNumbersMarked(numbersToCheck)) {
            alert("Pyramid correctly claimed");
        } else {
            this.boogie();
        }
    }

    checkforFullHouse() {
        if (this.checkAllNumbersMarked(this.ticketNumbers.flat())) {
            alert("Full House correctly claimed");
        } else {
            this.boogie();
        }
    }

    checkforBreakfast(){
        if (this.checkForBLD(0, 2)) {
            alert("Breakfast correctly claimed");
        } else {
            this.boogie();
        }
    }
    checkforLunch(){
        if (this.checkForBLD(3, 5)) {
            alert("Lunch correctly claimed");
        } else {
            this.boogie();
        }
    }
    checkforDinner(){
        if (this.checkForBLD(6, 8)) {
            alert("Dinner correctly claimed");
        } else {
            this.boogie();
        }
    }

    checkForBLD(colStart, colEnd) {
        let numbersToCheck = [];
        for (let i = 0; i <= 2; i++) {
            for (let j = colStart; j <= colEnd; j++) {
                if (this.allTicketNumbers[i][j] !== 0) {
                    numbersToCheck.push(this.allTicketNumbers[i][j]);
                }
            }
        }
        return this.checkAllNumbersMarked(numbersToCheck);
    }

    checkforYounger(){
        let allNumbers = this.ticketNumbers.flat();
        let numbersToCheck = [];
        let indexCount = 0;
        for (let i = 0; i < allNumbers.length; i++) {
            if (allNumbers[i] <= 45) {
                numbersToCheck[indexCount] = allNumbers[i];
                indexCount++;
            }
        }
        if (this.checkAllNumbersMarked(numbersToCheck)) {
            alert("Younger correctly claimed");
        } else {
            this.boogie();
        }
    }
    checkforOlder(){
        let allNumbers = this.ticketNumbers.flat();
        let numbersToCheck = [];
        let indexCount = 0;
        for (let i = 0; i < allNumbers.length; i++) {
            if (allNumbers[i] > 45) {
                numbersToCheck[indexCount] = allNumbers[i];
                indexCount++;
            }
        }
        if (this.checkAllNumbersMarked(numbersToCheck)) {
            alert("Older correctly claimed");
        } else {
            this.boogie();
        }
    }
    checkforRaindrop(){
        let currentNumberPresent = false;
        for (let j = 0; j < 9; j++) {
            let numberMarkedInColumn = false;
            for (let i = 0; i < 3; i++) {
                if (this.allTicketNumbers[i][j] === 0) continue;
                if (this.roundNumbers.has(this.allTicketNumbers[i][j])) {
                    numberMarkedInColumn = true;
                    if (this.allTicketNumbers[i][j] === this.currentNumber) {
                        currentNumberPresent = true;
                    }
                    break;
                }
            }
            if (!numberMarkedInColumn) {
                this.boogie();
                return;
            }
        }
        if (currentNumberPresent) {
            alert("Raindrop correctly claimed");
        } else {
            this.boogie();
        }

    }


    checkAllNumbersMarked(numbersToCheck) {
        this.checkNumbersMarked(numbersToCheck, numbersToCheck.length);
    }

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