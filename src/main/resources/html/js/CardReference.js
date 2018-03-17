let cardReference = ["name","id","description","url",
    "price","life","attack","nbrAttackPerTurn","cardType"];

function validateCardDataAsJson(jsonObject) {
    for (let i = 0 ; i < cardReference.length ; i++) {
        if (!jsonObject[cardReference[i]]) {
            return false;
        }
    }
    return true;
}

let cardInGameReference = ["id","uid"];

function validateCardInGameAsJson(jsonObject) {
    for (let i = 0 ; i < cardReference.length ; i++) {
        if (!jsonObject[cardReference[i]]) {
            return false;
        }
    }
    return true;
}

$.get('/cards', function(data) {
    console.log(data);
});