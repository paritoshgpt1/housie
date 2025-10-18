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
        this.claim = null;
        this.ticketNumber = null;
    }

    // function checkDividends: checks if the player has hit any dividend
    // 'claim' is the dividend 'code' from backend (snake_case)
    checkDividends(claim, ticketNumber) {
        this.claim = claim;
        this.ticketNumber = ticketNumber;
        const fn = this[claim];
        if (typeof fn === 'function') {
            fn.call(this);
        } else {
            console.warn('No validator implemented for claim code:', claim);
            alert('Validation for this claim is not available yet.');
        }
    }

    // function checkForEarly7: checks if the player has hit the Early7 dividend
    // early_7: Any seven numbers on the ticket are called, including current
    early_7() {
        if (this.checkNumbersMarked(this.ticketNumbers.flat(), 7)) {
            alert("Early 7 correctly claimed");
            this.correctlyClaimed();
        } else {
            this.boogie();
        }
    }

    // four_corners: four corner numbers are called
    four_corners() {
        let numbersToCheck = [];
        numbersToCheck.push(this.ticketNumbers[0][0]);
        numbersToCheck.push(this.ticketNumbers[0][4]);
        numbersToCheck.push(this.ticketNumbers[2][0]);
        numbersToCheck.push(this.ticketNumbers[2][4]);
        if (this.checkAllNumbersMarked(numbersToCheck)) {
            alert("4 Corners correctly claimed");
            this.correctlyClaimed();
        } else {
            this.boogie();
        }
    }

    // bamboo: center column all three numbers are called
    bamboo() {
        let numbersToCheck = [];
        numbersToCheck.push(this.ticketNumbers[0][2]);
        numbersToCheck.push(this.ticketNumbers[1][2]);
        numbersToCheck.push(this.ticketNumbers[2][2]);
        if (this.checkAllNumbersMarked(numbersToCheck)) {
            alert("Bamboo correctly claimed");
            this.correctlyClaimed();
        } else {
            this.boogie();
        }
    }

    // l: left column + entire bottom row
    l() {
        let numbersToCheck = [];
        numbersToCheck.push(this.ticketNumbers[0][0]);
        numbersToCheck.push(this.ticketNumbers[1][0]);
        numbersToCheck = numbersToCheck.concat(this.ticketNumbers[2]);
        if (this.checkAllNumbersMarked(numbersToCheck)) {
            alert("L correctly claimed");
            this.correctlyClaimed();
        } else {
            this.boogie();
        }
    }

    // t: top row + center column
    t() {
        let numbersToCheck = [];
        numbersToCheck = numbersToCheck.concat(this.ticketNumbers[0]);
        numbersToCheck.push(this.ticketNumbers[1][2]);
        numbersToCheck.push(this.ticketNumbers[2][2]);
        if (this.checkAllNumbersMarked(numbersToCheck)) {
            alert("T correctly claimed");
            this.correctlyClaimed();
        } else {
            this.boogie();
        }
    }

    // h: left column + middle row + right column
    h() {
        let numbersToCheck = [];
        numbersToCheck.push(this.ticketNumbers[0][0]);
        numbersToCheck.push(this.ticketNumbers[2][0]);
        numbersToCheck = numbersToCheck.concat(this.ticketNumbers[1]);
        numbersToCheck.push(this.ticketNumbers[0][4]);
        numbersToCheck.push(this.ticketNumbers[2][4]);
        if (this.checkAllNumbersMarked(numbersToCheck)) {
            alert("H correctly claimed");
            this.correctlyClaimed();
        } else {
            this.boogie();
        }
    }

    // top_line: top row
    top_line() {
        if (this.checkAllNumbersMarked(this.ticketNumbers[0])) {
            alert("Top Line correctly claimed");
            this.correctlyClaimed();
        } else {
            this.boogie();
        }
    }

    // middle_line: middle row
    middle_line() {
        if (this.checkAllNumbersMarked(this.ticketNumbers[1])) {
            alert("Middle Line correctly claimed");
            this.correctlyClaimed();
        } else {
            this.boogie();
        }
    }

    // bottom_line: bottom row
    bottom_line() {
        if (this.checkAllNumbersMarked(this.ticketNumbers[2])) {
            alert("Bottom Line correctly claimed");
            this.correctlyClaimed();
        } else {
            this.boogie();
        }
    }

    // zona: none of the numbers on ticket have been called
    zona() {
        let numbersToCheck = this.ticketNumbers.flat();
        for (let i = 0; i < numbersToCheck.length; i++) {
            if(this.roundNumbers.has(numbersToCheck[i])) {
                this.boogie();
                return;
            }
        }
        alert("Zona correctly claimed");
        this.correctlyClaimed();
    }

    // temp: min and max numbers on ticket are called
    temp() {
        let numbersToCheck = [];
        numbersToCheck.push(Math.min.apply(this, this.ticketNumbers.flat()));
        numbersToCheck.push(Math.max.apply(this, this.ticketNumbers.flat()));
        if (this.checkAllNumbersMarked(numbersToCheck)) {
            alert("Temperature correctly claimed");
            this.correctlyClaimed();
        } else {
            this.boogie();
        }
    }

    // pyramid: top center, middle-left/right, and bottom row
    pyramid() {
        let numbersToCheck = [];
        numbersToCheck.push(this.ticketNumbers[0][2]);
        numbersToCheck.push(this.ticketNumbers[1][1]);
        numbersToCheck.push(this.ticketNumbers[1][3]);
        numbersToCheck.push(this.ticketNumbers[2][0]);
        numbersToCheck.push(this.ticketNumbers[2][2]);
        numbersToCheck.push(this.ticketNumbers[2][4]);
        if (this.checkAllNumbersMarked(numbersToCheck)) {
            alert("Pyramid correctly claimed");
            this.correctlyClaimed();
        } else {
            this.boogie();
        }
    }

    // full_house: all numbers on ticket
    full_house() {
        if (this.checkAllNumbersMarked(this.ticketNumbers.flat())) {
            alert("Full House correctly claimed");
            this.correctlyClaimed();
        } else {
            this.boogie();
        }
    }

    // breakfast: first three columns (non-zero cells)
    breakfast(){
        if (this.checkForBLD(0, 2)) {
            alert("Breakfast correctly claimed");
            this.correctlyClaimed();
        } else {
            this.boogie();
        }
    }
    // lunch: middle three columns
    lunch(){
        if (this.checkForBLD(3, 5)) {
            alert("Lunch correctly claimed");
            this.correctlyClaimed();
        } else {
            this.boogie();
        }
    }
    // dinner: last three columns
    dinner(){
        if (this.checkForBLD(6, 8)) {
            alert("Dinner correctly claimed");
            this.correctlyClaimed();
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

    // younger: all numbers <= 45
    younger(){
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
            this.correctlyClaimed();
        } else {
            this.boogie();
        }
    }
    // older: all numbers > 45
    older(){
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
            this.correctlyClaimed();
        } else {
            this.boogie();
        }
    }
    // raindrop: at least one number per column, must include current number
    raindrop(){
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
                }
            }
            if (!numberMarkedInColumn) {
                this.boogie();
                return;
            }
        }
        if (currentNumberPresent) {
            alert("Raindrop correctly claimed");
            this.correctlyClaimed();
        } else {
            this.boogie();
        }

    }


    checkAllNumbersMarked(numbersToCheck) {
        return this.checkNumbersMarked(numbersToCheck, numbersToCheck.length);
    }

    // function checkNumbersMarked: checks whether the numbers to check have been actually announced or not
    // numbersToCheck: list of numbers to be checked
    // count: the expected count of numbers to be marked
    checkNumbersMarked(numbersToCheck, count) {
        console.log(numbersToCheck, count);
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

    correctlyClaimed() {
        $.post(
            "/claims",
            {
                name: this.claim,
                ticketId: this.ticketNumber
            },
            function() {
                console.log("Claim successfully saved");
            }
        );
    }
}
