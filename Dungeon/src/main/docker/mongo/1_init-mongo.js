const user = {
    user: 'mongouser',
    pwd: 'mongop@ss',
    roles: [{
        role: 'readWrite',
        db: 'dungeon'
    }]
};

db.createUser(user);
