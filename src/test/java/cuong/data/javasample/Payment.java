package cuong.data.javasample;
// Generated Aug 2, 2016 10:52:30 AM by Hibernate Tools 4.3.4.Final

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Payment generated by hbm2java
 */
@Entity
@Table(name = "payment", catalog = "classicmodels")
public class Payment implements java.io.Serializable {

    private PaymentId id;
    private Customer customer;
    private Date paymentDate;
    private BigDecimal amount;

    public Payment() {
    }

    public Payment(PaymentId id, Customer customer, Date paymentDate, BigDecimal amount) {
        this.id = id;
        this.customer = customer;
        this.paymentDate = paymentDate;
        this.amount = amount;
    }

    @EmbeddedId

    @AttributeOverrides({
            @AttributeOverride(name = "customerNumber", column = @Column(name = "customerNumber", nullable = false)),
            @AttributeOverride(name = "checkNumber", column = @Column(name = "checkNumber", nullable = false, length = 50)) })
    public PaymentId getId() {
        return this.id;
    }

    public void setId(PaymentId id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerNumber", nullable = false, insertable = false, updatable = false)
    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "paymentDate", nullable = false, length = 0)
    public Date getPaymentDate() {
        return this.paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    @Column(name = "amount", nullable = false, precision = 10)
    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}