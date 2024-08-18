class Api {
    constructor() {
        this._commonUrlPart = 'http://localhost:8080';
    }
    _checkResult(result) {
      if (result.ok) {
        return result.json();
      }
      else {
       return Promise.reject(`Ошибка: ${result.status}`);
      } 
    }
  
    async generateMaze(rows, cols) {
      const result = await fetch(`${this._commonUrlPart}/maze?rows=${rows}&cols=${cols}`, {});
        return this._checkResult(result);
    }

   async generateCave(rows, cols, probability) {
    const result = await fetch(`${this._commonUrlPart}/cave?rows=${rows}&cols=${cols}&probability=${probability}`, {});
    return this._checkResult(result);
    }

    async getNewCaveGeneration(cave, lifeLimit, deathLimit) {
      const result = await fetch(`${this._commonUrlPart}/cave?lifeLimit=${lifeLimit}&deathLimit=${deathLimit}`, {
          method: 'POST',
          headers: {
              'Content-Type': 'application/json'
          },
          body: JSON.stringify(cave)
        });
        return this._checkResult(result);
    }
  
  }
  
  export const api = new Api();