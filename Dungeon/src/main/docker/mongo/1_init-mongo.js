const user = {
    user: 'mongouser',
    pwd: 'mongop@ss',
    roles: [{
        role: 'readWrite',
        db: 'test'
    }]
};

db.createUser(user);
