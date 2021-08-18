const user = {
    user: 'mongouser',
    pwd: 'mongop@ss',
    roles: [{
        role: 'readWrite',
        db: 'book'
    }]
};

db.createUser(user);
