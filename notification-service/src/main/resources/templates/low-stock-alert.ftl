<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Low Stock Alert</title>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
        .container { width: 100%; max-width: 600px; margin: 0 auto; }
        .header { background-color: #f44336; color: white; padding: 10px 20px; }
        .content { padding: 20px; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Low Stock Alert</h1>
        </div>
        <div class="content">
            <p>Dear Admin,</p>
            <p>This is to inform you that <strong>${count}</strong> ingredients are currently below their minimum stock threshold.</p>
            
            <table>
                <thead>
                    <tr>
                        <th>Ingredient</th>
                        <th>Current Stock</th>
                        <th>Minimum Threshold</th>
                        <th>Unit</th>
                    </tr>
                </thead>
                <tbody>
                    <#list ingredients as ingredient>
                    <tr>
                        <td>${ingredient.name}</td>
                        <td>${ingredient.currentStock}</td>
                        <td>${ingredient.minimumStock}</td>
                        <td>${ingredient.unitOfMeasure}</td>
                    </tr>
                    </#list>
                </tbody>
            </table>
            
            <p>Please take necessary actions to replenish the inventory.</p>
            
            <p>Regards,<br>Smart POS System</p>
        </div>
    </div>
</body>
</html>