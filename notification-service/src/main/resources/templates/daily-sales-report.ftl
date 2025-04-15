<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Daily Sales Report</title>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
        .container { width: 100%; max-width: 600px; margin: 0 auto; }
        .header { background-color: #4CAF50; color: white; padding: 10px 20px; }
        .content { padding: 20px; }
        .summary { background-color: #f2f2f2; padding: 15px; border-radius: 5px; margin-bottom: 20px; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Daily Sales Report</h1>
        </div>
        <div class="content">
            <p>Dear Manager,</p>
            <p>Here is the sales report for ${date}:</p>
            
            <div class="summary">
                <h3>Summary</h3>
                <p>Total Orders: <strong>${orderCount}</strong></p>
                <p>Total Sales: <strong>$${totalSales}</strong></p>
            </div>
            
            <h3>Order Details</h3>
            <table>
                <thead>
                    <tr>
                        <th>Order ID</th>
                        <th>Customer</th>
                        <th>Amount</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <#list orders as order>
                    <tr>
                        <td>${order.id}</td>
                        <td>${(order.customer.firstName)!''} ${(order.customer.lastName)!''}</td>
                        <td>$${order.total}</td>
                        <td>${order.status}</td>
                    </tr>
                    </#list>
                </tbody>
            </table>
            
            <p>For detailed reports, please log in to the management dashboard.</p>
            
            <p>Regards,<br>Smart POS System</p>
        </div>
    </div>
</body>
</html>